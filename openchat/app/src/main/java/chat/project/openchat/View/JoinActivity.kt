package chat.project.openchat.View

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import chat.project.openchat.Model.ApiService
import chat.project.openchat.Model.RequestHelper
import chat.project.openchat.Model.UserInfo
import chat.project.openchat.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_join.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class JoinActivity : AppCompatActivity() {

    private val TAG = "JoinActivity"

    //퍼미션 응답 처리 코드
    private val multiplePermissionsCode = 100

    //서버의 저장된 프로필 이미지 명
    //갤러리에서 사진을 선택하지 않으면 기본 이미지 경로로 설정
    private var profile_path : String = "picture/default_image.png"

    //필요한 퍼미션 리스트
    //원하는 퍼미션을 이곳에 추가하면 된다.
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)


        //이용약관 동의 확인
        //id, 암호, 암호 재확인 입력 여부 확인
        //위의 조건을 만족하면 서버로 회원가입 요청 보내기
        //회원가입 완료되었다는 응답이 오면 바로 로그인 처리 하여 main activity로 전환
        //회원가입 실패가 뜰 경우 실패한 이유를 Toast로 안내
        //실패 이유: 1. 아이디가 존재 한다. / 2. 네트워크 통신 과정에서 오류. / 3. 서버 장애

        //onCreate함수내에서 퍼미션 체크및 권한 요청 함수 호출
        checkPermissions()

        //회원가입 버튼 클릭 시 서버로 사용자 정보를 전달하여 회원가입 완료 한다.
        //회원가입이 완료 되면 로그인 처리하고 메인 화면으로 이동 한다.
        signBtn.setOnClickListener {
            //회원가입 정보를 모두 입력 했다면 회원가입 가능여부 서버통신
            if(!signagreeCB.isChecked)
            {
                //약관동의를 안했다면 실행
                Toast.makeText(this,"약관을 읽고 동의 해주세요.", Toast.LENGTH_SHORT).show()
                //signagreeCB로 이동
                signagreeCB.requestFocus()
            }
            else if (signidEt.text.toString().equals("")) {
                Toast.makeText(this,"아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                //signidEt로 커서 이동
                signidEt.requestFocus()

                //키보드 보이게 하는 부분
                val imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

            }
            else if(signpwEt.text.toString().equals(""))
            {
                Toast.makeText(this,"암호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                //signpwEt로 커서 이동
                signpwEt.requestFocus()

                //키보드 보이게 하는 부분
                val imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

            }
            else if(signrepwEt.text.toString().equals(""))
            {
                Toast.makeText(this,"재확인 암호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                //signrepwEt로 커서 이동
                signrepwEt.requestFocus()

                //키보드 보이게 하는 부분
                val imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

            }
            else {

                postJoinUser(signidEt.text.toString(),signpwEt.text.toString(), profile_path )
                //버튼을 누를 때마다 기존 내용 초기화
                signidEt.setText("")
                signpwEt.setText("")
                signrepwEt.setText("")

            }
        }


        profileIv.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 200)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK) {
            if (requestCode == 200) {

                val file = File(data?.data?.path)
                val filepath = file.path.split(":")
                var image_id: String? = filepath[filepath.size - 1]
                Log.e("joinActivity profile path  => ", data?.data?.path)
                Log.e("joinActivity profile image_id => ", image_id)
                Log.e("joinActivity profile uri => ", data?.data.toString())

                val column = "_data"
                val projection = arrayOf(column)

                var cursor = contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Images.Media._ID + " = ? ",
                    arrayOf(image_id),
                    null
                )

                if (cursor != null) {

                    //커서를 처음 위치로 이동 시키고, 디바이스에 저장되어 있는 이미지 경로를 가져온다.
                    cursor.moveToFirst()

                    val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                    var realPath: String? = cursor.getString(columnIndex)

                    Log.e("joinActivity profile realPath => ", realPath)
                    Log.e("joinActivity profile columnIndex => ", columnIndex.toString())

                    if (realPath != null) {
                        setImage(realPath)
                    }
                    //mediastore.images.media.data is deprecated
                    //var imagePath: String? = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    //Log.e("joinActivity profile imagePath => ", imagePath)
                }

                Glide.with(this)
                    .load(data?.data)
                    .override(400, 400)
                    .into(profileIv)
            }
        }
    }


    //퍼미션 체크 및 권한 요청 함수
    private fun checkPermissions() {
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
    }

    //권한 요청 결과 함수
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            multiplePermissionsCode -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.e("TAG", "권한 획득 실패 : $permission")
                            Log.e("TAG", "권한을 허용하지 않으면 서비스 이용이 불가 합니다.")
                        }
                    }
                }
            }
        }
    }


    /* 레트로핏으로 서버통신하는 부분 */
    //갤러리에서 선택한 이미지 서버로 전송하여 서버에 저장.
    private fun setImage(imagePath : String) {

        //---------------------- 서버로 이미지 전송 --------------------------------------------------------------------------
        val upload_file = File(imagePath)

        val requestBody = upload_file.asRequestBody("*/*".toMediaTypeOrNull())
        val filetoUpload: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", upload_file.name, requestBody)

        val filename =
            upload_file.name.toRequestBody("text/plain".toMediaTypeOrNull())

        var request = RequestHelper()
        val api = request.getRetrofit2()?.create(ApiService::class.java)

        api?.uploadFile(filetoUpload, filename)?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "=== setImage onFailure === ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.body() != null && response.code() == 200) {

                    val result: ResponseBody = response.body()!!
                    Log.e(TAG, "=== 서버에 프로필 이미지 저장 요청 성공 isSuccessful!!!!!!!!!!! === body : ${result}}")
                    Log.e(TAG, "=== 서버에 프로필 이미지 저장 요청 성공 isSuccessful!!!!!!!!!!! === body code : ${response.code()}")

                    if(!result.equals("0"))
                    {
                        // picture/1621933268.jpg 형식으로 서버에 저장된 이미지명을 보내준다.
                        profile_path =  result.string()
                        Log.e(TAG, "=== 서버에 프로필 이미지 저장 성공 === : ${profile_path}")
                    }
                    else
                    {
                        Log.e(TAG, "=== 서버에 프로필 이미지 저장 실패 ===")
                    }

                } else {
                    Log.e(TAG, "=== 서버에 프로필 이미지 저장 널 코드 === : ${response.code()}")
                }
            }

        })

    }

    //사용자 회원가입 서버에 요청
    private fun postJoinUser(id : String, pw : String, imagePath: String) {

        var request = RequestHelper()
        val api = request.getRetrofit2()?.create(ApiService::class.java)

        api?.postJoinUser(id, pw, imagePath)?.enqueue(object : Callback<UserInfo> {
            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                Log.e(TAG, "=== joinUser onFailure === ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {

                if (response.body() != null && response.code() == 200) {

                    val result: String = response.body().toString()
                    Log.e(TAG, "=== joinUser loadData() 성공 isSuccessful!!!!!!!!!!! === body : $result}")
                    Log.e(TAG, "=== joinUser loadData() 성공 isSuccessful!!!!!!!!!!! === body code : ${response.code()}")

                    val idx : Int = response.body()!!.idx
                    val id : String? = response.body()!!.id
                    val profile_path : String? = response.body()!!.profilePath
                    Log.e(TAG, "=== user_info loadData() 성공 isSuccessful!!!!!!!!!!! === id : $id")
                    Log.e(TAG, "=== user_info loadData() 성공 isSuccessful!!!!!!!!!!! === profile_path : $profile_path")

                    //로그인 정보가 일치 하지 않을 경우 서버에서는 id를 ""(빈문자열) 로 응답힌다.
                    if(id.equals(""))
                    {
                        Toast.makeText(this@JoinActivity,"이미 사용중인 ID 입니다.",Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "====== 회원가입 실패 ======")
                    }
                    else
                    {
                        var intent = Intent(this@JoinActivity, MainActivity::class.java)

                        intent.putExtra("idx", idx)
                        intent.putExtra("id", id)
                        intent.putExtra("profile_path", profile_path)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    Log.e(TAG, "=== 회원가입 널 코드 === : ${response.code()}")
                }
            }

        })

    }

    //저장소에 있는 이미지 가져오는 방법
    //                val filePath = applicationInfo.dataDir + File.separator + System.currentTimeMillis()
////
////                val file = File(filePath)
////
////
////                try{
////                        val inputStream = contentResolver.openInputStream(data?.data!!)
////                        if (inputStream == null)
////                        {
////
////                        }
////                        // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
////                        val outputStream = FileOutputStream(file)
////                        var buf = byteArrayOf(1024.toByte())
////
////                        var len : Int
////
////                        len = inputStream?.read(buf)!!
////
////                        while ((len) > 0)
////                        {
////                            outputStream.write(buf, 0, len)
////
////                            len = inputStream?.read(buf)!!
////                        }
////
////                        outputStream.close()
////                        inputStream.close()
////                }catch (e : IOException)
////                {
////                    Log.e("joinActivity IOException  => ",e.toString())
////                }
////
////                Log.e("joinActivity profile real path  => ",file.getAbsolutePath())
////                Log.e("joinActivity profile real path  => ",filePath)

}
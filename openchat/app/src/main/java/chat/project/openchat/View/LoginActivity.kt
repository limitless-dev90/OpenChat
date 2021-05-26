package chat.project.openchat.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import chat.project.openchat.Model.ApiService
import chat.project.openchat.Model.RequestHelper
import chat.project.openchat.Model.UserInfo
import chat.project.openchat.R
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //var => 값이 변하는 변수라는 의미
        //String => non-nullable (null 값이 올 수 없는 변수, null 값이 들어가면 컴파일 에러 발생.)
        //String? => nullable (null 값이 올 수 있는 변수)
        var id : String
        var pw : String


        //로그인 버튼을 클릭하면 앱의 메인 Activity로 전환한다.
        //화면 전환 시 LoginActivity는 필요 없으므로 finish() 하여 종료한다.
        loginBtn.setOnClickListener {

            id = idEt.text.toString()
            pw = pwEt.text.toString()

            Log.e(TAG, id)
            Log.e(TAG, pw)

            //id와 pw가 빈문자열이 아니면 서버에 로그인 정보를 전달
            //회원정보가 일치하면 메인 activity로 전환 및 login activity 종료
            if(!id.equals("") && !pw.equals(""))
            {
                RequestLoginCheck(id,pw)

            }
            else
            {
                Toast.makeText(this,"아이디와 암호를 입력해주세요.",Toast.LENGTH_SHORT).show()
            }

        }

        //회원가입 버튼을 클릭하면 회원가입 Activity로 전환한다.
        joinBtn.setOnClickListener {

            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
            finish()
        }


        //id 스페이스바 입력 방지
        idEt.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_SPACE)
            {
                idEt.setText("")
                Toast.makeText(this,"ID에는 빈문자열을 포함 할 수 없습니다.",Toast.LENGTH_SHORT).show()
                Log.e(TAG, "스페이스바입력")
                return@setOnKeyListener true
            }
            else
            {
                Log.e(TAG, "그외 입력")
                return@setOnKeyListener false
            }

        }

        //pw 스페이스바 입력 방지
        pwEt.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_SPACE)
            {
                pwEt.setText("")
                Toast.makeText(this,"암호에는 빈문자열을 포함 할 수 없습니다.",Toast.LENGTH_SHORT).show()
                Log.e(TAG, "스페이스바입력")
                return@setOnKeyListener true
            }

            Log.e(TAG, "그외 입력")
            return@setOnKeyListener false
        }

    }


    /* 레트로핏으로 서버통신하는 부분 */
    private fun RequestLoginCheck(id : String, pw : String) {

        var request = RequestHelper()
        val api = request.getRetrofit2()?.create(ApiService::class.java)

        api?.postLoginCheck(id,pw)?.enqueue(object : Callback<UserInfo> {
            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                Log.e(TAG, "=== login onFailure === ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {

                if (response.body() != null && response.code() == 200) {

                    val result: String = response.body().toString()
                    Log.e(TAG, "=== login loadData() 성공 isSuccessful!!!!!!!!!!! === body : $result}")
                    Log.e(TAG, "=== login loadData() 성공 isSuccessful!!!!!!!!!!! === body code : ${response.code()}")

                    val idx : Int = response.body()!!.idx
                    val id : String? = response.body()!!.id
                    val profile_path : String? = response.body()!!.profilePath
                    Log.e(TAG, "=== user_info loadData() 성공 isSuccessful!!!!!!!!!!! === id : $id")
                    Log.e(TAG, "=== user_info loadData() 성공 isSuccessful!!!!!!!!!!! === profile_path : $profile_path")

                    //로그인 정보가 일치 하지 않을 경우 서버에서는 id를 ""(빈문자열) 로 응답힌다.
                    if(id.equals(""))
                    {
                        Toast.makeText(this@LoginActivity,"로그인 정보가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "======로그인 정보가 일치하지 않습니다.======")
                    }
                    else
                    {
                        var intent = Intent(this@LoginActivity, MainActivity::class.java)

                        intent.putExtra("idx", idx)
                        intent.putExtra("id", id)
                        intent.putExtra("profile_path", profile_path)
                        startActivity(intent)
                        finish()
                    }


                } else {
                    Log.e(TAG, "=== login loadData() 널 코드 === : ${response.code()}")
                }
            }

        })

    }

}
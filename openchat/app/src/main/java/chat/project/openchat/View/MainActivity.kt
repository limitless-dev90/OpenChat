package chat.project.openchat.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import chat.project.openchat.Adapter.ChatListAdapter
import chat.project.openchat.Model.*
import chat.project.openchat.R
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var chatListAdapter: ChatListAdapter   //activity 리싸이클러뷰에 연결할 어댑터

    //사용자 정보를 저정할 userModel 객체 (idx, id, profilePath 정보 저장)
    var user_info : UserInfo? = UserInfo()
    private var roomList = mutableListOf<RoomList>() //오픈 채팅방 목록을 저장하는 list 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //서버에서 오픈채팅방 정보를 응답받아서 리싸이클러뷰로 화면에 뿌려준다.
        initRecycler()

        val intent = getIntent()

        user_info!!.idx = intent.getIntExtra("idx",0)
        user_info?.id = intent.getStringExtra("id")
        user_info?.profilePath = intent.getStringExtra("profile_path")

        Log.e(TAG," id => "+user_info?.id)
        Log.e(TAG," profile_path => "+user_info?.profilePath)


    }

    private fun initRecycler() {

        //리싸이블러뷰에 어댑터 연결
        chatListAdapter = ChatListAdapter(this)
        chatListReCyclerView.adapter = chatListAdapter

        //클릭리스너 등록
        chatListAdapter.setItemClickListener( object : ChatListAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                Log.e("MainActivity", "===== ${position}번 리스트 선택 ========")

                val intent = Intent(this@MainActivity, ChatActivity::class.java)


                intent.putExtra("room_id", roomList.get(position).roomIdx)
                intent.putExtra("idx", user_info!!.idx)
                intent.putExtra("id", user_info?.id)
                intent.putExtra("profile_path", user_info?.profilePath)

                startActivity(intent)
            }
        })

        //서버와 통신하여 오픈채팅방 정보를 응답 받는다.
        LoadRoom()
    }


    /* 레트로핏으로 서버통신하는 부분 */
    private fun LoadRoom() {

        var request = RequestHelper()
        val api = request.getRetrofit2()?.create(ApiService::class.java)

        api?.getRoom()?.enqueue(object : Callback<ChatListModel> {
            override fun onFailure(call: Call<ChatListModel>, t: Throwable) {
                Log.e(TAG, "=== room load onFailure === ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<ChatListModel>, response: Response<ChatListModel>) {

                if (response.body() != null && response.code() == 200) {

                    val result: String = response.body().toString()
                    Log.d(TAG, "=== roomList loadData() 성공 isSuccessful!!!!!!!!!!! === body : $result}")
                    Log.d(TAG, "=== roomList loadData() 성공 isSuccessful!!!!!!!!!!! === body code : ${response.code()}")

                    var room_list : List<RoomList>? = response.body()!!.roomList
                    Log.d(TAG, "=== roomList loadData() 성공 isSuccessful!!!!!!!!!!! === product_List : $room_list")

                    roomList = room_list as MutableList<RoomList>

                    //오픈 채팅방 리스트 어댑터에 리스트 값 저장 및 리싸이클러뷰 갱신
                    chatListAdapter.datas = roomList
                    chatListAdapter.notifyDataSetChanged()

                } else {
                    Log.e(TAG, "=== roomList loadData() 널 코드 === : ${response.code()}")
                }
            }

        })

    }
}
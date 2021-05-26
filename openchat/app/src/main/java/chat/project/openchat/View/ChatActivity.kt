package chat.project.openchat.View

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import chat.project.openchat.Adapter.ChatAdapter
import chat.project.openchat.Model.*
import chat.project.openchat.R
import chat.project.openchat.TCPClient.SendDataThread
import chat.project.openchat.TCPClient.TCPClient_1N
import kotlinx.android.synthetic.main.activity_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatActivity : AppCompatActivity() {

    private val TAG = "ChatActivity"

    //핸들러 상태값
    //상태값이 1이면 핸들러에서는 ReceiveDataThread에서 전달받은 메세지를 화면에 뿌려주는 역활을 한다.
    val MESSAGE_RECEIVE = 1

    //activity 리싸이클러뷰에 연결할 어댑터
    lateinit var chatAdapter: ChatAdapter

    //사용자 정보를 저정할 userModel 객체 (idx, id, profilePath 정보 저장)
    var user_info : UserInfo? = UserInfo()

    //오픈채팅방 내용을 저장하는 List
    private var chatList = mutableListOf<ChatList>() //오픈 채팅방 대화 내용 저장

    var room_id : Int = 0                           //룸 index 값 저장

    var TCPClient : TCPClient_1N? = null            //TCP서버에 소켓연결을 요청하는 객체 선언
    var TCPClient_t : Thread? = null                //TCP서버에 소켓연결을 요청하는 객체의 run()함수를 동작시키기 위한 쓰레드 객체 선언
    var send_t : SendDataThread? = null             //연결된 소켓을 통해서 TCP서버로 메세지를 전송할 객체 선언

    //ReceiveDataThread를 통해서 다른 클라이언트가 전송한 메세지(TCP서버에서 응답한 메세지)를 채팅방에 표시하기 위한 핸들러
    var handler : Handler? = Handler{

        lateinit var receiveMessage : String

        when(it.what){
                MESSAGE_RECEIVE->{
                    Log.e(TAG,"handler message receive => " + it.obj.toString())
                    receiveMessage = it.obj.toString()
                }
        }

        if(!receiveMessage.contains("퇴장") && !receiveMessage.contains("접속"))
        {
            //현재 시간 구하기
            var now : LocalDateTime = LocalDateTime.now()
            var time : String = now.format(DateTimeFormatter.ofPattern("a hh시 mm분"))

            //[0] => user_idx, [1] => user_id , [2] => profile_path , [3] => message
            var receive_info_arr = receiveMessage.split("|")

            Log.e(TAG,"receive_info_arr[0] => " + receive_info_arr[0])
            Log.e(TAG,"receive_info_arr[1] => " + receive_info_arr[1])
            Log.e(TAG,"receive_info_arr[2] => " + receive_info_arr[2])
            Log.e(TAG,"receive_info_arr[3] => " + receive_info_arr[3])

            //내가 보낸 메세지도 TCP서버에서 보내므로,
            // 내가 보낸 메세지가 아닐 경우에만 chatList에 메세지를 저장하고 화면에 표시한다.
            if(user_info?.idx != receive_info_arr[0].toInt())
            {
                //클라이언트 오픈채팅방 화면에 클라이언트가 입력한 내용 추가 하기
                var receiveData : ChatList? = ChatList(receive_info_arr[0].toInt(), receive_info_arr[1], receive_info_arr[3], time, receive_info_arr[2], false)
                chatList.add(receiveData!!)
                chatContentReCyclerView.scrollToPosition(chatList.size - 1)
                chatAdapter.notifyDataSetChanged()
            }
        }


        true
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e(TAG, "=== onDestroy() 호출  ===")
        send_t = SendDataThread(TCPClient?.client, TCPClient?.oos, "/quit")

        //Thread 객체를 생성할때 SendDateThread를 인자로 넣어주고 생성 후에
        //Thread 변수 t에 대입
        var t : Thread? = Thread(send_t)
        t?.start() //쓰레드 시작
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intent = getIntent()

        room_id = intent.getIntExtra("room_id", 0)

        user_info!!.idx = intent.getIntExtra("idx",0)
        user_info?.id = intent.getStringExtra("id")
        user_info?.profilePath = intent.getStringExtra("profile_path")

        Log.e(TAG," room_id => "+room_id.toString())
        Log.e(TAG," idx => "+user_info!!.idx)
        Log.e(TAG," id => "+user_info?.id)
        Log.e(TAG," profile_path => "+user_info?.profilePath)

        //TCPClient 연결
        TCPClient = TCPClient_1N(room_id.toString(), user_info!!.id, "10.0.2.2", user_info?.profilePath, user_info?.idx.toString(), handler)//MainClient객체 생성시 문자열 3개를 넣어서 객체를 생성(room_id,user_id, ip)

        TCPClient_t = Thread(TCPClient)
        //Thread 객체를 생성할때 ReceiveDateThread를 인자로 넣어주고 생성 후에
        //Thread 변수 t에 대입
        TCPClient_t?.start() //쓰레드 시작

        //리싸이클러뷰 데이터 연결
        initRecycler()

        //보내기 버튼을 클릭하면 서버로 내용을 보내서 DB에 저장한다.
        //채팅방을 나갔다가 들어왔을 때, 채팅 내용을 로드하기 위함.
        sendIb.setOnClickListener {

            if(sendEt.text.isNotEmpty())
            {
                var sendData = sendEt.text.toString()

                Log.e(TAG, "=== InsertChat 성공!! ===")
                InsertChat(sendData)    //서버로 보낸 내용을 보내고 DB에 저장

                send_t = SendDataThread(TCPClient?.client, TCPClient?.oos, sendData)    //TCP 서버로 메세지를 전송하기 위한 쓰레드

                //Thread 객체를 생성할때 SendDateThread를 인자로 넣어주고 생성 후에
                //Thread 변수 t에 대입
                var t : Thread? = Thread(send_t)
                t?.start() //쓰레드 시작
                Log.e(TAG, "=== TCP 서버로 메세지 보내기 성공!! ===")

                //현재 시간 구하기
                var now : LocalDateTime = LocalDateTime.now()

                var time : String = now.format(DateTimeFormatter.ofPattern("a hh시 mm분"))

                //클라이언트 오픈채팅방 화면에 클라이언트가 입력한 내용 추가 하기
                var myContent : ChatList? = ChatList(user_info?.idx, user_info?.id, sendData, time, user_info?.profilePath, true)
                chatList.add(myContent!!)
                chatContentReCyclerView.scrollToPosition(chatList.size - 1)
                chatAdapter.notifyDataSetChanged()

            }
            else
            {
                Toast.makeText(this,"채팅창에 내용을 입력해주세요!", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "=== sendEt.text is Empty ===")
            }

        }

        //처음 editText click 했을때 리스너가 반응 하려면 isFocusable => false 로 초기화 해주면 된다.
        //처음에 click 반응이 없는 문제가 있었음
        sendEt.setOnFocusChangeListener { v, hasFocus -> Boolean
            if(hasFocus)
            {
                chatContentReCyclerView.scrollToPosition(chatList.size - 1)
                Log.e(TAG, "=== sendEt OnFocus !!! ===")
            }

        }

        //채팅 editText를 클릭하면 채팅내용 맨 아래로 이동
        sendEt.setOnClickListener {

            chatContentReCyclerView.scrollToPosition(chatList.size - 1)
            Log.e(TAG, "=== sendEt Click !!! ===")
        }



    }

    private fun initRecycler() {

        //어댑터를 리싸이클러뷰의 연결
        chatAdapter = ChatAdapter(this)
        chatContentReCyclerView.adapter = chatAdapter

        //서버에 요청하여 오픈 채팅방 채팅 내용을 응답 받는다.
        LoadChat()
    }


    /* 레트로핏으로 서버통신하는 부분 */

    //오픈 채팅방 정보를 서버에 요청하여 응답 받는다.
    private fun LoadChat() {

        var request = RequestHelper()
        val api = request.getRetrofit2()?.create(ApiService::class.java)

        api?.getChat(room_id)?.enqueue(object : Callback<ChatModel> {
            override fun onFailure(call: Call<ChatModel>, t: Throwable) {
                Log.e(TAG, "=== chat load onFailure === ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<ChatModel>, response: Response<ChatModel>) {

                if (response.body() != null && response.code() == 200) {

                    val result: String = response.body().toString()
                    Log.d(TAG, "=== chatList loadData() 성공 isSuccessful!!!!!!!!!!! === body : $result}")
                    Log.d(TAG, "=== chatList loadData() 성공 isSuccessful!!!!!!!!!!! === body code : ${response.code()}")

                    var chat_list : List<ChatList>? = response.body()!!.chatList
                    Log.d(TAG, "=== chatList loadData() 성공 isSuccessful!!!!!!!!!!! === product_List : $chat_list")

                    chatList = chat_list as MutableList<ChatList>

                    for(chat in chatList)
                    {
                        //로그인한 사용자가 작성한 채팅이면 isMyChat true 값으로 변경
                        //isMyChat => defalt value : false
                        if(user_info?.id.equals(chat.id))
                        {
                            chat.isMyChat = true
                        }

                    }

                    //오픈 채팅방 리스트 어댑터에 리스트 값 저장 및 리싸이클러뷰 갱신
                    chatAdapter.datas = chatList

                    chatContentReCyclerView.scrollToPosition(chatList.size - 1)
                    chatAdapter.notifyDataSetChanged()

                } else {
                    Log.e(TAG, "=== chatList loadData() 널 코드 === : ${response.code()}")
                }
            }

        })

    }


    //서버에 클라이언트가 입력한 채팅 내용을 저장한다.
    private fun InsertChat(content : String) {

        var request = RequestHelper()
        val api = request.getRetrofit2()?.create(ApiService::class.java)

        api?.postInsertChat(user_info!!.idx,room_id,content)?.enqueue(object : Callback<Int> {
            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.e(TAG, "=== chat insert onFailure === ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<Int>, response: Response<Int>) {

                if (response.body() != null && response.code() == 200) {

                    val result: Int = response.body()!!
                    Log.d(TAG, "=== chat insert loadData() 성공 isSuccessful!!!!!!!!!!! === body : ${result.toString()}}")
                    Log.d(TAG, "=== chat insert loadData() 성공 isSuccessful!!!!!!!!!!! === body code : ${response.code()}")

                    if(result == 1)
                    {
                        sendEt.setText("")
                        Log.e(TAG, "=== 채팅 등록 성공 ===")
                    }
                    else
                    {
                        sendEt.setText("")
                        Toast.makeText(this@ChatActivity,"채팅 등록 실패....", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "=== 채팅 등록 실패 ===")
                    }


                } else {
                    Log.e(TAG, "=== chatList loadData() 널 코드 === : ${response.code()}")
                }
            }

        })

    }



}
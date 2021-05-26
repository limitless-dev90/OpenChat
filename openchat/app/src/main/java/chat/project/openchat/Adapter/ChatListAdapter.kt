package chat.project.openchat.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import chat.project.openchat.Model.RoomList
import chat.project.openchat.R
import com.bumptech.glide.Glide

//채팅방 목록 리싸이클러뷰에 연결할 어댑터
//아이템을 구성하는 레이아웃을 인플러이터 하고 데이터를 바인딩 한다.
class ChatListAdapter (private val context: Context) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    var datas = mutableListOf<RoomList>()

    //오픈채팅방 목록 아이템 레이아웃을 인플레이터하여 뷰홀더에 담는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chatlist_item,parent,false)
        return ViewHolder(view)
    }

    //어뎁터에 연결되어 있는 list 개수를 리턴하는 함수 (오픈채팅방 목록 개수)
    //오픈 채팅방 목록이 10개 이면 datas.size 값은 10
    override fun getItemCount(): Int = datas.size

    //onCreateViewHolder 함수는 내용이 없는 껍데기만 만들었다면
    //onBindViewHolder 함수에서 각 아이템에 내용을 채워넣습니다. (뷰홀더 하나를 아이템이라고 표현한다.)
    //내용이란: 채팅방 명, 채탕방 이미지 경로, 채팅방 참여자수
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // inner class ViewHolder(view: View) 클래스의 bind 함수를 호출하여
        //사용자아이디, 사용자 프로필이미지 경로, 채팅 내용, 채팅 시간 값을 뷰홀더에 채워 넣는다.
        holder.bind(datas[position])

        //itemView에 onClickListner를 달고, 그 안에서 직접 만든 itemClickListener를 연결시킨다.
        //직접 만든 리스너를 연결시키는 이유는 activity onCreate() 함수에서 리스너 처리를 하기 위함.
        //activity 단에서 리스너를 처리해야 intent를 활용하여 채팅방 activity로 전환 및 데이터를 전달하기 수월하기 때문에
        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)
        }
    }

    //뷰홀더 클래스
    //뷰홀더와 연결된 레이아웃에다가 내용을 채워넣는 역할을하는 클래스
    //내용이란: 채팅방 명, 채탕방 이미지 경로, 채팅방 참여자수
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val roomImg: ImageView = itemView.findViewById(R.id.room_imgIv)
        private val roomName: TextView = itemView.findViewById(R.id.room_idTv)
        private val roomCount: TextView = itemView.findViewById(R.id.room_countTv)

        fun bind(item: RoomList) {
            roomName.text = item.roomName
            roomCount.text = item.roomCount.toString()
            Glide.with(itemView).load(item.roomImgPath).override(100, 100).into(roomImg)

        }
    }

    //클릭 인터페이스 정의
    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }


}
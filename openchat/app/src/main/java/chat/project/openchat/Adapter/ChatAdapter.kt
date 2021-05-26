package chat.project.openchat.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import chat.project.openchat.Model.ChatList
import chat.project.openchat.R
import com.bumptech.glide.Glide

//채팅방 내용 리싸이클러뷰에 연결할 어댑터
//아이템을 구성하는 레이아웃을 인플러이터 하고 데이터를 바인딩 한다.
class ChatAdapter (private val context: Context) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var datas = mutableListOf<ChatList>()


    //채팅 아이템 레이아웃을 인플레이터하여 뷰홀더에 담는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_item,parent,false)
        return ViewHolder(view)
    }

    //어뎁터에 연결되어 있는 list 개수를 리턴하는 함수 (채팅 목록 개수)
    //오픈 채팅방에 대화 내용이 10개 이면 datas.size 값은 10
    override fun getItemCount(): Int = datas.size

    //onCreateViewHolder 함수는 내용이 없는 껍데기만 만들었다면
    //onBindViewHolder 함수에서 각 아이템에 내용을 채워넣습니다. (뷰홀더 하나를 아이템이라고 표현한다.)
    //내용이란: 사용자아이디, 사용자 프로필이미지 경로, 채팅 내용, 채팅 시간
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // inner class ViewHolder(view: View) 클래스의 bind 함수를 호출하여
        //사용자아이디, 사용자 프로필이미지 경로, 채팅 내용, 채팅 시간 값을 뷰홀더에 채워 넣는다.
        holder.bind(datas[position])
    }

    //뷰홀더 클래스
    //뷰홀더와 연결된 레이아웃에다가 내용을 채워넣는 역할을하는 클래스
    //내용이란: 사용자아이디, 사용자 프로필이미지 경로, 채팅 내용, 채팅 시간
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val idTv_L: TextView = itemView.findViewById(R.id.idTv_L)
        private val messageTv_L: TextView = itemView.findViewById(R.id.messageTv_L)
        private val timeTv_L: TextView = itemView.findViewById(R.id.timeTv_L)
        private val profileIv_L: ImageView = itemView.findViewById(R.id.profileIv_L)

        private val idTv_R: TextView = itemView.findViewById(R.id.idTv_R)
        private val messageTv_R: TextView = itemView.findViewById(R.id.messageTv_R)
        private val timeTv_R: TextView = itemView.findViewById(R.id.timeTv_R)
        private val profileIv_R: ImageView = itemView.findViewById(R.id.profileIv_R)

        fun bind(item: ChatList) {
            //왼쪽 레이아웃 => 사용자 메세지 (본인)
            //오른쪽 레이아웃 => 다른 사용자들 메세지
            Log.e("ChatAdapter", "===== ${item.id} ========")
            if(item.isMyChat)
            {
                Log.e("ChatAdapter", "===== 사용자 본인 ========")
                //왼쪽 레이아웃 보이게 처리
                idTv_L.visibility = View.VISIBLE
                messageTv_L.visibility = View.VISIBLE
                timeTv_L.visibility = View.VISIBLE
                profileIv_L.visibility = View.VISIBLE

                //오른쪽 메세지 레이아웃 숨기기
                idTv_R.visibility = View.GONE
                messageTv_R.visibility = View.GONE
                timeTv_R.visibility = View.GONE
                profileIv_R.visibility = View.GONE

                idTv_L.text = item.id
                messageTv_L.text = item.message
                timeTv_L.text = item.time

                Glide.with(itemView).load(item.profilePath).override(60, 60).into(profileIv_L)
            }
            else
            {
                Log.e("ChatAdapter", "===== 다른사용자 ========")
                //오른쪽 메세지 레이아웃 보이게 처리
                idTv_R.visibility = View.VISIBLE
                messageTv_R.visibility = View.VISIBLE
                timeTv_R.visibility = View.VISIBLE
                profileIv_R.visibility = View.VISIBLE

                //왼쪽 레이아웃 숨기기
                idTv_L.visibility = View.GONE
                messageTv_L.visibility = View.GONE
                timeTv_L.visibility = View.GONE
                profileIv_L.visibility = View.GONE

                idTv_R.text = item.id
                messageTv_R.text = item.message
                timeTv_R.text = item.time

                Glide.with(itemView).load(item.profilePath).override(60, 60).into(profileIv_R)
            }


        }
    }


}
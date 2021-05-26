package chat.project.openchat.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//오픈채팅방 목록 정보를 저장하는 모델 클래스
//레트로핏에서 서버의 응답 받는 부분에서 Model 클래스를 이용
//Model 클래스에서 정의된 변수로 서버에서 응답을 하면 레트로핏에서 Model의 정의된 변수명으로 데이터를 받을 수 있다.
//*주의: 서버에서 Model에서 정의된 변수 대로 응답을 하지 않으면 에러 발생
class ChatListModel {
    @SerializedName("total_room")
    @Expose
    var totalRoom: Int? = null

    @SerializedName("room_list")
    @Expose
    var roomList: List<RoomList>? = null

}

class RoomList {
    @SerializedName("roomImgPath")
    @Expose
    var roomImgPath: String? = null

    @SerializedName("roomName")
    @Expose
    var roomName: String? = null

    @SerializedName("roomCount")
    @Expose
    var roomCount: Int? = null

    @SerializedName("roomIdx")
    @Expose
    var roomIdx: Int? = null

}
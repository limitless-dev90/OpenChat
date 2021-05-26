package chat.project.openchat.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//사용자 정보를 저장하는 모델 클래스
//레트로핏에서 서버의 응답 받는 부분에서 Model 클래스를 이용
//Model 클래스에서 정의된 변수로 서버에서 응답을 하면 레트로핏에서 Model의 정의된 변수명으로 데이터를 받을 수 있다.
//*주의: 서버에서 Model에서 정의된 변수 대로 응답을 하지 않으면 에러 발생
class UserInfo  {
    @SerializedName("idx")
    @Expose
    var idx: Int = 0

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("profilePath")
    @Expose
    var profilePath: String? = null
}
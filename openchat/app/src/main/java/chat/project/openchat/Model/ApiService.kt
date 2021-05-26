package chat.project.openchat.Model

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

//레트로핏2 서버 요청 함수 제공 인터페이스
interface ApiService {
    @Multipart
    @POST("/OpenChat/loginCheck.php")
    fun postLoginCheck(
        /* 파라미터 2개인 경우 */
        @Part("id")
        id: String
        ,
        @Part("pw")
        pw: String
    ): Call<UserInfo>

    @Multipart
    @POST("/OpenChat/joinUser.php")
    fun postJoinUser(
        /* 파라미터 3개인 경우 */
        @Part("id")
        id: String
        ,
        @Part("pw")
        pw: String
        ,
        @Part("profile_image")
        profile_image: String
    ): Call<UserInfo>

    @GET("/OpenChat/room.php")
    fun getRoom(): Call<ChatListModel>

    @GET("/OpenChat/loadOpenchat.php")
    fun getChat(@Query("room_idx") room_id: Int): Call<ChatModel>


    @Multipart
    @POST("/OpenChat/insert_chat.php")
    fun postInsertChat(
        /* 파라미터 2개인 경우 */
        @Part("user_idx")
        user_idx: Int
        ,
        @Part("room_idx")
        room_idx: Int
        ,
        @Part("content")
        content: String
    ): Call<Int>

    @Multipart
    @POST("/OpenChat/upload_image.php")
    fun uploadFile(
        @Part file: MultipartBody.Part?,
        @Part("file") name: RequestBody?
    ): Call<ResponseBody>

}
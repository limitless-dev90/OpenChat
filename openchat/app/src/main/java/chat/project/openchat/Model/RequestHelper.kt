package chat.project.openchat.Model

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//레트로핏2를 생성하는 클래스
//getRetrofit2() 함수를 호출하면 생성된 retrofit 객체를 리턴한다.
class RequestHelper {

    val API_URL = "http://10.0.2.2" // localhost 웹서버 주소

    var retrofit: Retrofit? = null

    fun getRetrofit2(): Retrofit? {

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit
    }

}
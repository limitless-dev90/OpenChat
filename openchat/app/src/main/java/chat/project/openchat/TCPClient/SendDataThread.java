package chat.project.openchat.TCPClient;

import android.util.Log;
import java.io.PrintWriter;
import java.net.Socket;

//TCP서버에 메세지를 전달할 쓰레드 클래스
public class SendDataThread implements Runnable{

    String TAG = "SendDataThread";

    Socket client; //사용자 Socket 정보를 담는 변수
    PrintWriter oos; // 서버에 메세지를 보낼 PrintWriter 변수
    String sendData; // 서버에 보낼 메세지 정보를 담을 변수

    //SendDataThread 생성자 (사용자 소켓, PrintWriter, 서버에보낼 메세지 정보를 매개변수로 받는다.)
    public SendDataThread(Socket s, PrintWriter oos, String sendData){
        client = s;
        this.oos = oos;
        this.sendData = sendData;
    }

    //쓰레드 동작 run() 함수
    //쓰레드.start() 하면 run() 함수가 실행 됩니다.
    public void run(){

        try{
            //키보드 입력을 받아서 sendData에 대입
            oos.println( sendData );
            oos.flush();
            Log.e(TAG,"=== 서버로 보낸 메세지 : "+ sendData);
        }catch(Exception e){
            //예외처리 발생시 실행
            Log.e(TAG,"에러발생: "+e.toString());//예외처리시 출력
        }
    }
}

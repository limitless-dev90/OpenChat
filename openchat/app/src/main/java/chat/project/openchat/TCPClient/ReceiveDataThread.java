package chat.project.openchat.TCPClient;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import android.os.Handler;

//TCP서버에서 전달하는 메세지를 받는 쓰레드 클래스
public class ReceiveDataThread implements Runnable{

    String TAG = "ReceiveDataThread";

    //채팅방 activity에서 선언된 핸들러에게 받은 메세지를 전달하기 위한 상태 값
    //핸들러에서는 ReceiveDataThread에서 전달한 메세지를 화면에 뿌려주는 역활을 한다.
    final int MESSAGE_RECEIVE = 1;

    Socket client; //사용자 Socket 정보를 담는 변수
    BufferedReader ois; // 서버에서 보낸 메세지 정보를 받을 BufferReader 변수
    String receiveData; // 서버에서 보낸 메세지 내용을 저장할 변수
    Handler handler;    // 채팅방 activity에서 선언한 핸들러에게 서버에게 받은 메세지를 전달하기 위한 Handler 변수


    //ReceiveDataThread 생성자 (사용자 소켓, 버퍼리더, 핸들러 정보를 매개변수로 받는다.)
    public ReceiveDataThread(Socket s, BufferedReader ois, Handler handler){
        client = s;
        this.ois = ois;
        this.handler = handler;
    }

    //쓰레드 동작 run() 함수
    //쓰레드.start() 하면 run() 함수가 실행 됩니다.
    public void run(){

        try{
            //서버에서 보낸 메세지를 받는 부분
            //서버에서 사용자 소켓을 삭제하면 while을 빠져 나간다.
            // 서버에게 /quit 문자열을 보내면 서버에서 소켓을 삭제한다.
            while( ( receiveData = ois.readLine() ) != null )
            {
                Log.e(TAG,"=== 받은 메세지 : "+ receiveData);
                Message msg = handler.obtainMessage(); //메인스레드 핸들러의 메시지 객체 가져오기
                msg.what = MESSAGE_RECEIVE; // 메시지 아이디 설정
                msg.obj = receiveData; // 메시지 내용 설정
                handler.sendMessage(msg); // 채팅방 activity 핸들러로 메시지 보내기
            }


        }catch(Exception e){
            //예외처리 발생시 실행
            Log.e(TAG,"에러발생: "+e.toString());//예외처리시 출력
        } finally{

            try{
                Log.e(TAG,"=== finally 실행 : ReceiveDataThread 종료 ===");
                ois.close(); //BufferReader 객체 종료
                client.close(); //Socket 객체 종료
            }catch(IOException ioe){
                //예외처리 발생시 실행(IOException 시)
                Log.e(TAG,"에러발생: "+ioe.toString());
            }
        }
    }
}

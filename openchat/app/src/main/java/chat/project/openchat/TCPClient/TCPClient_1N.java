package chat.project.openchat.TCPClient;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import android.os.Handler;

//TCP 서버에 소켓 연결을 요청하고 소켓정보를 저장하는 클래스
public class TCPClient_1N implements Runnable{

    String TAG = "TCPClient_1N";

    public String ipAddress; //서버 IP 주소
    static final int port=5001; //TCP 서버에서 서비스를 제공하는 포트
    public Socket client=null; //사용자 Socket 정보를 담는 변수
    public PrintWriter oos; //TCP서버에 메세지를 보낼 PrintWriter 변수
    public BufferedReader ois; //TCP서버에서 보내는 메세지를 읽을 때 필요한 BufferReader 변수
    public String room_id; // 사용자가 들어온 방의 index값을 저장하는 변수
    public  String user_id; // 사용자 id를 저장하는 변수
    public ReceiveDataThread rt; // TCP서버에서 보내는 메세지를 받을 ReceiveDateThread 변수
    public Handler handler;     //채탕방 activity에서 선언된 handler 객체를 저장 (채팅방 화면에 받은 내용을 출력하는 핸들러)

    public String profile_path; //사용자 프로필이미지 경로 저장하는 변수
    public String user_idx;     //사용자 index 값을 저장하는 변수


    //TCPClient_1N 생성자 (방 index, 사용자id, TCP서버 IP, 사용자 프로필이미지경로, 사용자 index, 채팅방 activity handler)
    public TCPClient_1N(String _room_id, String id, String ip, String profile_path, String user_idx, Handler handler) {

        room_id=_room_id;
        user_id=id;
        ipAddress=ip;
        this.handler = handler;
        this.profile_path = profile_path;
        this.user_idx = user_idx;
    }

    //쓰레드 동작 run() 함수
    //쓰레드.start() 하면 run() 함수가 실행 됩니다.
    @Override
    public void run() {
        try{//시도하다.
            Log.e(TAG,"**** 클라이언트*****");
            //Socket 객체를 생성
            client = new Socket(ipAddress, port);

            // 생성된 소켓에서 InputStream 정보를 받고 해당 InputStream을 InputStreamReader 객체의 파라미터로 전달하여 BufferedReader 생성
            //charset을 EUC-KR 로 설정하기 위해서 InputStreamReader 객체를 이용. (그렇지 않으면 한글이 깨지는 문제 발생)
            ois = new BufferedReader( new InputStreamReader( client.getInputStream(), "EUC-KR") );


            // 생성된 소켓에서 getOutputStream 정보를 받고 해당 getOutputStream을 OutputStreamWriter 객체의 파라미터로 전달하여 PrintWriter 생성
            //charset을 EUC-KR 로 설정하기 위해서 OutputStreamWriter 객체를 이용. (그렇지 않으면 한글이 깨지는 문제 발생)
            oos = new PrintWriter( new OutputStreamWriter(client.getOutputStream(), "EUC-KR") );

            //oos.println("TCP서버로 보낼 메세지 내용") 함수를 이용하여 PrintWirter에 TCP서버로 전송할 메세지 저장.
            oos.println( room_id + "&" + user_id + "&" + profile_path + "&" + user_idx);

            //PrintWirter에 저장되어 있는 메세지를 연결된 소켓을 통해 TCP서버로 전송한다.
            oos.flush();

            //ReceiveDataThread 객체를 생성
            rt= new ReceiveDataThread(client, ois, this.handler);

            //ReceiveDataThread를 실행시킬(run함수 호출하기 위한) Thread 객체 생성
            Thread t = new Thread(rt);
            t.start(); //ReceiveDataThread 쓰레드 시작 => run()함수 동작

        } catch(Exception e){
            //예외처리시 실행
            Log.e(TAG,"에러발생: "+e.toString());
        }finally {
            Log.e(TAG,"=== TCPClient finally 실행 ===");
        }
    }

}
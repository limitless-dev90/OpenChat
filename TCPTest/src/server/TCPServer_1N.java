package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

//1대 N tcp 통신이 되는 서버의 메인 소스코드
public class TCPServer_1N {

	
	int port = 5001; // 정수변수 port선언후 port 번호 대입 
	ServerSocket server = null; // ServerSocket타입 server 선언 후 null 대입 
	Socket child = null; // Socket타입 child 선언 후 null 대입
	
	HashMap<String, HashMap<String, PrintWriter>> hm;
	HashMap<String, PrintWriter> sub_hm; //컬렉션인 HashMap타입의 키값을 String 값은 PrintWriter인 hm 변수 선언 
	
	public TCPServer_1N() { 
		//ChatServer 생성자 
		ChatServerThread sr; //ChatServerThread타입에 sr 변수 선언 
		//브로드 캐스팅을 하기위한 쓰레드 객체 
		Thread t; //Thread 타입의 t 변수 선언 
		try { //시도하다 
				server = new ServerSocket( port ); //서버소켓을 생성해서 server 변수에 대입 
				System.out.println( "**************************************" );//출력 
				System.out.println( "* 채팅 서버 *" );//출력 
				System.out.println( "**************************************" );//출력 
				System.out.println( "클라이언트의 접속을 기다립니다." );//출력 
				hm = new HashMap<String, HashMap<String, PrintWriter>>(); //hashMap객체를 생성해서 hm 변수에 대입 
				while( true ) { 
					// 무한 반복
					child = server.accept(); //ServerSocket의 변수인 server를 이용하여 accept함수 호출을 하여 
					//클라이언트 접속시까지 대기를 합니다. 
					//접속시에는 클라이언트와 연결 됩니다. 
					//클라이언트의 소켓을 연결받습니다. 
					if( child != null ) { 
						
						BufferedReader ois; // BufferReader 클래스 타입의 변수 ois 선언 
						PrintWriter oos; // PrintWriter 클래스 타입의 변수 oos 선언 
						
						ois = new BufferedReader( new InputStreamReader( child.getInputStream() ) ); 
						//BufferReader 객체를 생성시 InputStreamReader 객체로 인자를 받고 
						//InputStreamReader 객체를 생성시에는 child(Socket)에 getInputStream()함수를 호출하면 
						//InputStream을 리턴하여 인자로 받고 InputStreamReader 객체를 생성 
						// BufferReader로 생성된 객체를 ois에 대입 
						oos = new PrintWriter( child.getOutputStream() ); 
						//PrintWriter 객체를 생성시에는 child(Socket)에 getOutputStream()함수를 호출하면 
						//OutputStream을 리턴하여 인자로 받고 PrintWriter 객체를 생성 
						//PrintWriter로 생성된 객체를 oos에 대입 
						String user_info = ois.readLine(); //ois의 readLine함수를 호출하여 한줄의 문자열을 읽어서 user_info에 대입
						System.out.println("user_info => " + user_info );//출력 
						
						//[0] => room_id , [1] => user_id , [2] => user_profile_path, [3] => user_idx
						String[] info_arr = user_info.split("&");
						System.out.println("info_arr 배열 길이 => " + String.valueOf(info_arr.length));//출력
						System.out.println("info_arr[0] room_id => " + info_arr[0]);//출력
						System.out.println("info_arr[1] user_id => " + info_arr[1] );//출력
						System.out.println("info_arr[2] profile_path => " + info_arr[2] );//출력
						System.out.println("info_arr[3] user_idx => " + info_arr[3] );//출력
						
						//기존에 있는 오픈 채팅방이면 해당 채팅방에 사용자 목록을 꺼내서 sub_hm 변수에 대입
						if(hm.containsKey(info_arr[0]))
						{
							sub_hm = hm.get(info_arr[0]);
						}
						else
						{
							//기존에 없는 오픈 채팅방이면 사용자 목록을 저장할 hashmap 객체를 생성하여 sub_hm 변수에 대입
							sub_hm = new HashMap<String, PrintWriter>();
							//생성한 객체를 오픈채팅방 목록을 저장하고 있는 hm 변수에 put
							hm.put(info_arr[0], sub_hm);
						}
						
						//Socket타입에 변수인 child가 null 값이 아니면 실행 
						//child에는 클라이언트 소켓과 연결을 할 수 있는 소켓입니다. 
						sr = new ChatServerThread( child, sub_hm, info_arr[1], info_arr[2], info_arr[3] ); 
						//ChatSverThread 객체를 Socket과 HashMap을 받아서 생성 후에
						//ChatSverThread의 변수인 sr에 대입 
						t = new Thread(sr); 
						//Thread객체를 ChatSverThread을 받아서 생성후 
						//Thread의 변수인 t에 대입 
						t.start();//쓰레드 시작 
					} 
				} 
			} catch ( Exception e ) { 
				//예외처리가 발생하면 실행 
				e.printStackTrace(); //예외처리 출력 
			} 
		} 

	public static void main(String[] args) 
	{ 
		new TCPServer_1N(); //MainServer 객체 생성 
	}

}

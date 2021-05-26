package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient_1N {
	
	String ipAddress; //문자열 변수 ipAdress 선언 
	static final int port=5001; // static final 정수 변수 port 선언후 port 번호 초기화 
	Socket client=null; // Socket 변수 client 선언 후 null 값 초기화 
	BufferedReader read; // BufferReader 변수 read 선언 
	PrintWriter oos; //PrintWriter 변수 oos 선언 
	BufferedReader ois; //BufferReader 변수 ois 선언 
	String sendData; // 문자열 변수 sendDate 선언 
	String receiveData; // 문자열 변수 receiveDate 선언 
	String room_id; // 문자열 변수 room_id 선언 
	String user_id; // 문자열 변수 user_id 선언 
	String profile_path;
	String user_idx;
	ReceiveDataThread rt; // ReceiveDateThread 변수 rt 선언 
	boolean endflag=false; // 참거짓 변수 endflag 선언 후 false로 초기화
	
	public TCPClient_1N(String _room_id, String id, String ip, String _profile_path, String _user_idx) {
		
		//MainClient 생성자로 인자로 문자열 3개를 받는다.
		room_id=_room_id; // user_id에 id 대입 
		user_id=id; // user_id에 id 대입 
		ipAddress=ip; // ipAddress에 ip 대입 
		profile_path = _profile_path;
		user_idx = _user_idx;
		
		try{//시도하다. 
				System.out.println("**** 클라이언트*****");//출력 
				client = new Socket(ipAddress, port); 
				//client(Socket)에 Socket 객체를 생성하여 대입 
				//Socket객체를 생성 할때 인자로 문자열 2개를 담아서 생성자 호출 
				read= new BufferedReader(new InputStreamReader(System.in)); 
				//BufferReader 객체를 생성을 하는데 InputStrreamReader 객체를 만들어서 인자로 받는다. 
				//InputStreamReader는 System.in을 인자로 받아서 객체를 만든다. 
				//BufferReader객체가 생성되면 read에 대입
				ois = new BufferedReader( new InputStreamReader( client.getInputStream(),"EUC-KR")); 
				//BufferReader 객체를 생성시 InputStreamReader 객체로 인자를 받고 
				//InputStreamReader 객체를 생성시에는 child(Socket)에 getInputStream()함수를 호출하면 
				//InputStream을 리턴하여 인자로 받고 InputStreamReader 객체를 생성 
				// BufferReader로 생성된 객체를 ois에 대입 
				oos = new PrintWriter( new OutputStreamWriter(client.getOutputStream(), "EUC-KR") ); 
				//PrintWriter 객체를 생성시에는 child(Socket)에 getOutputStream()함수를 호출하면 
				//OutputStream을 리턴하여 인자로 받고 PrintWriter 객체를 생성 
				//PrintWriter로 생성된 객체를 oos에 대입 
				oos.println( room_id + "&" + user_id + "&" + profile_path + "&" + user_idx );
				//oos(PrintWriter)의 println함수를 user_id(문자열)을 넣어서 호출 
				//PrintWriter에 user_id(문자열)이 담긴다. 
				oos.flush();
				//oos(PrintWriter)의 함수 flush()를 호출한다. 
				//flush함수를 호출하면 PrintWirter에 담겨있던 
				//문자열을 연결된 Socket을 통해 전송하게 된다. 
				rt= new ReceiveDataThread(client, ois); 
				//ReceiveDataThread 객체를 생성할때 client(Socket)과 ois(BufferReader)를 
				//인자로 넣어줘서 객체를 생성하고 rt에 대입 
				Thread t = new Thread(rt); 
				//Thread 객체를 생성할때 ReceiveDateThread를 인자로 넣어주고 생성 후에 
				//Thread 변수 t에 대입 
				t.start(); //쓰레드 시작
				
				while(true){ 
					//무한 반복 
					sendData = read.readLine(); 
					//키보드 입력을 받아서 sendData에 대입 
					oos.println(sendData); 
					//oos(PrintWriter)의 println함수를 sendData(문자열)을 넣어서 호출 
					//PrintWriter에 sendDate(문자열)이 담긴다. 
					oos.flush(); 
					//oos(PrintWriter)의 함수 flush()를 호출한다. 
					//flush함수를 호출하면 PrintWirter에 담겨있던 
					//문자열을 연결된 Socket을 통해 전송하게 된다. 
					if(sendData.equals( "/quit") ) { 
						//sendData(문자열)dl /quit이면 아래 명령어 실행 
						endflag = true; //참거짓 변수 endflag에 true를 대입 
						break;// 반복문 탈출 
					} 
				} 
				
				System.out.print("클라이트의 접속을 종료합니다. ");//출력 
				System.exit( 0 );//프로그램 종료 
			} catch(Exception e){ 
				//예외처리시 실행 
				if(!endflag) e.printStackTrace();//예외처리시 출력 
			} finally{ 
				//위에 try catch 어떤상황이든 다끝나면 실행 
				try{//시도하다. 
						ois.close(); //BufferReader 객체 
						oos.close(); //PrintWriter 객체 oos close() 
						client.close(); //Socket 객체 client close() 
						System.exit(0); //프로그램을 종료합니다. 
				   }catch(IOException ioe){
					   //예외처리시 실행(IOException시) 
					   ioe.printStackTrace();//예외처리시 출력 
				   } 
			}
				   
	}
	
	public static void main(String[] args) { 
		Scanner sc = new Scanner(System.in); //스캐너 입력 
		System.out.print("방번호를 입력하세요 : ");//출력 
		String room_id = sc.next();//입력 받기 
		
		System.out.print("아이디를 입력하세요 : ");//출력 
		String id = sc.next();//입력 받기 
		
		System.out.print("프로필 이미지 경로를 입력하세요 : ");//출력 
		String profile_path = sc.next();//입력 받기 
		
		System.out.print("사용자 idx 를 입력하세요 : ");//출력 
		String user_idx = sc.next();//입력 받기 
		
		new TCPClient_1N(room_id, id, "127.0.0.1", profile_path, user_idx); //MainClient객체 생성시 문자열 객체를 생성
	}
	
}

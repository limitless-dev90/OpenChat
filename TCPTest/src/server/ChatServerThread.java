package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

//서버 쓰레드 소스 코드
//클라이언트에서 연결 요청이 들어오면  서버간 소켓이 연결
public class ChatServerThread implements Runnable 
{ 
	Socket child; //Socket 클래스 타입의 변수 child 선언 
	BufferedReader ois; // BufferReader 클래스 타입의 변수 ois 선언 
	PrintWriter oos; // PrintWriter 클래스 타입의 변수 oos 선언 
	String user_id; // 문자열 변수 user_id 선언 
	HashMap<String/*유저 ID*/, PrintWriter/*유저들 PrintWriter*/> hm; // 접속자 관리 
	//컬렉션 HashMap의 키값 String에 값PrintWriter의 변수 hm을 선언 
	InetAddress ip; // InetAddress 클래스 타입의 변수 ip 선언 
	String msg; // 문자열 변수 msg 선언
	String profile_path;
	String user_idx;
	
	public ChatServerThread(Socket s, HashMap<String, PrintWriter> h, String _user_id, String _profile_path, String _user_idx ) 
	{ 
		//ChatSverThread의 Socket과 HashMap을 인자로 받는 생성자 
		child = s; //인자로 받은 변수 s를 child에 대입(Socket) 
		hm = h; //인자로 받은 변수 h를 hm에 대입(HashMap)
		user_id = _user_id;
		profile_path = _profile_path;
		user_idx = _user_idx;
		
		try { 
				//시도한다. 
				ois = new BufferedReader( new InputStreamReader( child.getInputStream() ) ); 
				//BufferReader 객체를 생성시 InputStreamReader 객체로 인자를 받고 
				//InputStreamReader 객체를 생성시에는 child(Socket)에 getInputStream()함수를 호출하면 
				//InputStream을 리턴하여 인자로 받고 InputStreamReader 객체를 생성 
				// BufferReader로 생성된 객체를 ois에 대입 
				oos = new PrintWriter( child.getOutputStream() ); 
				//PrintWriter 객체를 생성시에는 child(Socket)에 getOutputStream()함수를 호출하면 
				//OutputStream을 리턴하여 인자로 받고 PrintWriter 객체를 생성 
				//PrintWriter로 생성된 객체를 oos에 대입 
				
				ip = child.getInetAddress(); 
				//child(Socket)을 통해서 getInetAddress()함수를 통해서 
				//Client IP 주소를 문자열로 받아 ip에 대입 
				System.out.println( ip + "로부터 " + user_id + "님이 접속하였습니다." );//출력 
				broadcast(user_id + "님이 접속하셨습니다."); //broadcast 함수 호출 호출을 할때 문자열 인자로 대입 
				
				synchronized( hm ) { 
					//임계영역 설정 함 
					//HashMap에 추가시 한 쓰레드만 들어와서 사용 가능함 
					hm.put( user_id, oos ); 
					//HashMap에 키:user_id(String) 값: oos(PrintWriter)를 추가함 
				} 
			} catch (Exception e ) {
				//예외처리 발생시 실행 
				e.printStackTrace();//예외 출력 
			} 
	} 
	
	public void run() { 
		String receiveData; 
		// 문자열 변수 receiveDate 선언 
		
		try//시도하다 
		{ 
			while( (receiveData = ois.readLine()) != null ) { 
				//ois의 readLine 함수를 호출하여 문자열 한줄 씩을 receiveDate에 대입을 하면 
				//receiveDate가 null이 아니면 계속 반복 
				//끝낼때 
				if( receiveData.equals( "/quit" ) ) 
				{ 
					//receiveDate가 /quit이면 아래 명령문 실행 
					synchronized( hm ) { 
						//임계영역 설정 함 
						//HashMap에 삭제시 한 쓰레드만 들어와서 사용 가능함 
						hm.remove( user_id ); //HashMap에 키값이 user_id인 것을 삭제하는 함수 
					} 
					
					break;//반복문 탈출 
				}//귓속말 
				else if( receiveData.indexOf( "/to" ) >= 0 ) { 
					//receiveDate의 함수 indexOf를 이용한 문자열에 있는지를 탐색 
					//만약에 있으면 0이상값을 주기 때문에 아래 명령어 실행 
					sendMsg( receiveData );//sendMsg함수를 receiveData를 인자로 받아서 호출 
				} //전체 메세지 보내기 
				else { 
					//위에 조건이 모두 아니면 아래명령문 실행 
					System.out.println(user_id + " >> " + receiveData );//출력 
					broadcast( user_idx + "|" + user_id + "|" + profile_path + "|" + receiveData ); //broadcast 함수 호출 호출을 할때 문자열 인자로 대입 
				} 
			} 
		} catch (Exception e ) {
			//예외처리 발생하면 실행 
			e.printStackTrace();//예외처리 출력 
		} finally {
			//위에 try catch 어떤상황이든 다끝나면 실행 
			synchronized( hm ) { 
				////임계영역 설정 함 
				//HashMap에 삭제시 한 쓰레드만 들어와서 사용 가능함 
				hm.remove( user_id ); //HashMap에 키값이 user_id인 것을 삭제하는 함수 
			} 
			broadcast( user_id + "님이 퇴장했습니다." ); //broadcast 함수 호출 호출을 할때 문자열 인자로 대입 
			System.out.println( user_id + "님이 퇴장했습니다." );//출력 
			
			try//시도하다 
			{ 
				if( child != null ) { 
					//child(Socket)이 만약에 null이 아니면 
					ois.close(); //BufferReader 객체 ois close() 
					oos.close(); //PrintWriter 객체 oos close() 
					child.close(); //Socket 객체 child close() 
				} 
			} catch ( Exception e) {}//예외처리 발생시 
		} 
	} 
	
	public void broadcast(String message){ 
		// 리턴을 하지않고 문자열을 인자로 받는 broadcast 함수 
		synchronized( hm ) { //임계영역 설정 함 
			try{ 
					for( PrintWriter oos : hm.values( )){ 
						//HashMap에서 값만 빼서 PrintWriter oos라는 변수에 대입을 한다. 
						//null이 나오기전까지 계속 반복한다. 
						oos.println( message ); 
						//oos(PrintWriter)의 함수 println에 문자열(message)을 넣는다. 
						//message(문자열)이 PrintWriter에 담긴다. 
						oos.flush(); 
						//oos(PrintWriter)의 함수 flush()를 호출한다. 
						//flush함수를 호출하면 PrintWirter에 담겨있던 
						//문자열을 연결된 Socket을 통해 전송하게 된다. 
					} //반복을 하면서 모든 연결된 소켓에 문자열을 송신하게 된다. 
				}catch(Exception e){ }//예외처리 발생시 실행 
		} 
	} 
	
	public void sendMsg(String message){ 
		// 리턴을 하지 않고 문자열을 인자로 받는 sendMsg 함수 
		int begin = message.indexOf(" ") + 1; 
		//처음 스페이스 그 다음 인덱스 숫자를 정수형 변수 begin에 대입 
		int end = message.indexOf(" ", begin); //begin에서 시작해 그 그다음 스페이스 있는 인덱스를 정수형 변수 end에 대입 
		
		if(end != -1){ 
			//정수형 변수 end가 -1이 아니면 실행 
			String id = message.substring(begin, end); 
			//문자열 변수 message을 begin에서 end만큰 잘라서 문자열 변수 id에 대입 
			String msg = message.substring(end+1); 
			//문자열 변수 message을 end에서 1을 더한 인덱스부터 끝까지 있는 문자열을 
			//문자열 변수 msg에 대입 
			PrintWriter oos = hm.get(id); 
			//PrintWriter의 변수 oos 에 HashMap에서 get 함수의 인자를 문자열 변수 id를 넣어서
			//Value이 PrintWriter을 빼서 oos에 대입 
			
			try{//시도하다 
					if(oos != null){ 
						//PrintWriter oos의 값이 널이 아니면 아래 명령문 실행 
						oos.println( user_id + "님이 다음과 같은 귓속말을 보내셨습니다. : " + msg ); 
						//oos(PrintWirter)에 println함수에 문자열을 담는다. 
						oos.flush(); //oos(PrintWriter)의 함수 flush()를 호출한다. 
						//flush함수를 호출하면 PrintWirter에 담겨있던 
						//문자열을 연결된 Socket을 통해 전송하게 된다. 
					}
				}catch(Exception e){ }//예외처리가 발생하면 실행  
		} 
	} 
}
					
			
		
	
				
			
		
	
				
			
			
		
		
				
					
			
			
		
	
			
			
		
	



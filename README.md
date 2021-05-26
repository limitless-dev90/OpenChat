# OpenChat
TCP, kotlin 학습을 위해서 간단한 OpenChat 프로젝트


# TCP 서버 (로컬서버) 구동 방법
환경 : windows10
사용포트 : 5001

구동 방법: 
TCPTest 파일을 아래 경로에 다운로드 받습니다.
C:\Java\project\

cmd 창을 켜고 C:\Java\project\TCPTest\src 경로로 이동

cd C:\Java\project\TCPTest\src

서버 구동 명령어
java server.TCPServer_1N

아래 이미지와 같이 표시되면 서버 구동 완료

![image](https://user-images.githubusercontent.com/75319175/119644102-7936d180-be57-11eb-9ded-b5980568fb85.png)

# netstat를 이용한 서버 구동 확인방법

netstat -an | find "5001"

![image](https://user-images.githubusercontent.com/75319175/119644526-e8acc100-be57-11eb-896e-92b0b82623a0.png)

# 클라이언트가 TCP 서버에 연결되었는지 확인 방법

클라이언트에서 TCP서버로 소켓 연결 요청

![image](https://user-images.githubusercontent.com/75319175/119644914-6bce1700-be58-11eb-9c93-8211edf58b4e.png)

TCP서버에서 소켓 연결 확인

![image](https://user-images.githubusercontent.com/75319175/119644890-6244af00-be58-11eb-9408-1d2d7dc42019.png)


netstat -an | find "5001"

![image](https://user-images.githubusercontent.com/75319175/119644777-3a554b80-be58-11eb-826f-14b9918620b1.png)

소켓이 연결되었다면, ESTABLISHED 상태도 연결된 정보 확인 가능.

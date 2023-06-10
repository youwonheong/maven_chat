import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

// 채팅 서버 클래스
public class ChatServerSocket {
    // private List <ChatHandlerObject> list;
    private ServerSocket server;

    // 클라이언트 리스트
    private List<ChatServerHandler> list;

    // 쓰레드 리스트
    private List<Thread> Thread_list;
    public static void main(String[] args) {

        ChatServerSocket chatserver = new ChatServerSocket();
        chatserver.serverNow();
    }

    public void serverNow(){
        try{
            server = new ServerSocket(9090);
            System.out.print("서버시작");
            list = new ArrayList<ChatServerHandler>();
            Thread_list = new ArrayList<Thread>();

            boolean flag = true;
            while(flag){
                Socket socket = server.accept();
                ChatServerHandler handler = new ChatServerHandler(socket,list);
                // 스레드 시작
                list.add(handler);
                Thread clientThread = new Thread(handler);
                clientThread.start();
                Thread_list.add(clientThread);
            }
        }
        catch (IOException e) {
            try{
                System.out.println("서버 프로세스중 오류가 발생했습니다. 서버를 종료합니다.");
                if (server != null)
                    server.close();
                System.exit(0);
            }
            catch (IOException ex){
                System.out.println("서버종료 중 오류가 발생했습니다. 강제종료합니다.");
                System.exit(0);
            }
        }
    }

}

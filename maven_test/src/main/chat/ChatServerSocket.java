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

    public static void main(String[] args) {

        ChatServerSocket chatserver = new ChatServerSocket();
        chatserver.serverNow();
    }

    public void serverNow(){
        try{
            server = new ServerSocket(9090);
            System.out.print("서버시작");
            list = new ArrayList<ChatServerHandler>();
            boolean flag = true;
            while(flag){
                Socket socket = server.accept();
                ChatServerHandler handler = new ChatServerHandler(socket,list);
                // 스레드 시작
                list.add(handler);
                new Thread(handler).start();
            }
        }
        catch (IOException e) {
            System.out.println("서버 프로세스중 오류가 발생했습니다.");
        }
    }

}

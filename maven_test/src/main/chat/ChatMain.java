// 클라이언트 메인 시작
public class ChatMain {
    public static void main(String[] args) {
        ChatClientSocket client = new ChatClientSocket();
        // 서버접속
        client.serverAccess();
        new Thread(client).start();
        client.chatKeying();
    }
}

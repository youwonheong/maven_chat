import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

// Runabled로 바꾸자
public class ChatServerHandler implements Runnable { // 서버 소켓을 처리함
    private ObjectInputStream reader;
    private ObjectOutputStream writer;
    private Socket socket;
    private List<ChatServerHandler> list;
    public ChatServerHandler(Socket socket,List<ChatServerHandler> list){
        // 연결된 클라이언트 정보 저장
        this.socket = socket;
        this.list = list;
    }
    public void run(){
        ChatInfoDTO cinfo = null;
        String nickName = "";

        try{
            boolean flag = true;
            while(flag){
                // 클라이언트에서 온 정보
                String recvStr = String.valueOf(reader.readObject());
                ObjectMapper mapper = new ObjectMapper();
                cinfo = mapper.readValue(recvStr,ChatInfoDTO.class);


                writer = new ObjectOutputStream(socket.getOutputStream());
                reader = new ObjectInputStream(socket.getInputStream());
                nickName = cinfo.getNickName();
                if(cinfo.getCommand() == Info.EXIT){
                    ChatInfoDTO send_info = new ChatInfoDTO();

                    send_info.setCommand(Info.EXIT);
                    writer.writeObject(send_info);
                    writer.flush();
                    if (reader != null)
                        reader.close();
                    if (writer != null)
                        writer.close();
                    if (socket != null)
                        socket.close();
                    // 클라이언트 리스트 제거
                    list.remove(this);

                    send_info.setCommand(Info.SEND);
                    send_info.setMessage(nickName+"님 퇴장하였습니다");
                    broadcast(send_info);
                    flag = false;
                }
                // 입장했을떄
                else if(cinfo.getCommand() == Info.JOIN){
                    ChatInfoDTO send_info = new ChatInfoDTO();
                    send_info.setCommand(Info.SEND);
                    send_info.setMessage(nickName+"님 입장하였습니다");
                    broadcast(send_info);
                } else if(cinfo.getCommand()==Info.SEND){
                    ChatInfoDTO send_info = new ChatInfoDTO();
                    send_info.setCommand(Info.SEND);
                    // 클라이언트에서 들어온 메시지
                    send_info.setMessage("["+nickName+"]"+ cinfo.getMessage());
                    broadcast(send_info);
                }
                else{
                    ChatInfoDTO send_info = new ChatInfoDTO();
                    send_info.setCommand(Info.SEND);
                    send_info.setMessage("ㅇㅇㅇ");
                    broadcast(send_info);
                }
            }
        }catch(IOException e){
            //예외처리
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            //예외처리
            e.printStackTrace();
        }
    }

    //다른 클라이언트에게 전체 메세지 보내주기
    public void broadcast(ChatInfoDTO sendDto) throws IOException {
        // 모든 클라이언트들에게 값 보내기
        for(ChatServerHandler handler: list){
            ObjectMapper mapper = new ObjectMapper();
            String sendDtoJsonData =mapper.writeValueAsString(sendDto);
            writer.writeObject(sendDtoJsonData);
            handler.writer.writeObject(sendDto); //핸들러 안의 writer에 값을 보내기
            handler.writer.flush();  //핸들러 안의 writer 값 비워주기
        }
    }


}

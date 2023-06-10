import java.io.*;

// 클라이언트와 서버간의 통신의 정보종류(입장, 퇴장, 메시지, 귓속말)
class ChatInfoDTO implements Serializable{
    // 닉네임
    private String nickName;
    // 메시지
    private String message;
    // 통신의 종류 지정
    private Info command;

    // 보내기
    public String getNickName(){
        return nickName;
    }
    public Info getCommand(){
        return command;
    }
    public String getMessage(){
        return message;
    }
    // 수정
    public void setNickName(String nickName){
        this.nickName= nickName;
    }
    public void setCommand(Info command){
        this.command= command;
    }
    public void setMessage(String message){
        this.message= message;
    }
}
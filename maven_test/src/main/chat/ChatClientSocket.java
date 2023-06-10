import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ChatClientSocket implements Runnable {
    private Socket socket;
    private ObjectInputStream reader=null;
    private ObjectOutputStream writer=null;

    // 클라이언트가 접속할 채팅서버 ip
    String serverIp = "";
    // 채팅에서 사용할 닉네임
    String nickname = "";
    // 서버ip및 닉네임을 입력하여 채팅서버에 접속한다.
    public void serverAccess(){


        Scanner sc = new Scanner(System.in);
        boolean flag = true;
        ObjectMapper mapper = new ObjectMapper();
        while(flag){
            System.out.println("서버IP를 입력하세요 :");
            serverIp = sc.nextLine();
            System.out.println("닉네임을 입력하세요 :");
            nickname = sc.nextLine();
            try{
                socket = new Socket(serverIp,9090);
                // 에러가 발생한다면
                reader= new ObjectInputStream(socket.getInputStream());
                writer = new ObjectOutputStream(socket.getOutputStream());
            }
            // 서버를 찾을수 없을떄
            catch (UnknownHostException e ){
                System.out.print("서버를 찾을 수 없습니다.");
                e.printStackTrace();
                continue;
            }
            // 연결이 되지 않았다면
            catch(IOException e){
                System.out.print("서버와 연결이 안되었습니다.");
                e.printStackTrace();
                continue;
            }
            // 서버 연결이 완료되었다면
            try{
                // 서버에 닉네임 보내기
                ChatInfoDTO cinfo = new ChatInfoDTO();
                cinfo.setCommand(Info.JOIN);
                cinfo.setNickName(nickname);
                String cinfoJsonData =mapper.writeValueAsString(cinfo);
                writer.writeObject(cinfoJsonData);
                writer.flush();
            }
            catch(IOException e){
                System.out.print("서버와 통신중 에러가 발생했습니다.");
                e.printStackTrace();
                continue;
            }
            // 서버연결이 무사히 완료되었다면 서버연결 반복문 종료
            flag = false;
            System.out.println("채팅접속이 완료되었습니다. 프로그램을 종료하시려면 EXIT를 입력하여주십시오.");
        }

    }
    // 채팅메시지를 입력 후 채팅서버 채팅메시지 데이터 전송 (EXIT를 보낼시 메소드 종료)
    public void chatKeying(){
        Scanner sc = new Scanner(System.in);
        boolean flag = true;
        ObjectMapper mapper = new ObjectMapper();
        while(flag){
                System.out.println(nickname + " : ");
                String chatMsg = sc.nextLine();
                try{
                    ChatInfoDTO cinfo = new ChatInfoDTO();
                    // 메시지 입력시 채팅서버로 보낸다
                    if(!chatMsg.equals("EXIT")){
                        // 입력한 메시지를 채팅서버로 보낸다
                        cinfo.setCommand(Info.SEND);
                        cinfo.setNickName(nickname);
                        cinfo.setMessage(chatMsg);

                        String cinfoJsonData =mapper.writeValueAsString(cinfo);
                        writer.writeObject(cinfoJsonData);
                        writer.flush();

                    }
                    // EXIT입력시 채팅프로그램 종료
                    else{
                        cinfo.setCommand(Info.EXIT);
                        cinfo.setNickName(nickname);
                        String cinfoJsonData =mapper.writeValueAsString(cinfo);
                        writer.writeObject(cinfoJsonData);
                        writer.flush();
                        flag = false;
                    }
                }
                catch (IOException e){
                    System.out.println("메시지를 전송하는 중 오류가 발생했습니다. 다시 입력하여 주십시오.");
                    continue;
                }
        }
        // 채팅 프로그램 종료
        try {
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
            if (socket != null)
                socket.close();
            System.out.println("채팅 종료^^");
        } catch (IOException e) {
            System.out.println("프로그램 종료 중 오류가 발생했습니다.");
        }
    }



    // 스레드로 메시지 출력
    public void run(){
        // 서버로부터 데이터 받기
        ChatInfoDTO cinfo = null;
        boolean flag = true;
        while(flag){
            try{
                // JackSON 라이브러리 사용시
                String recvStr = String.valueOf(reader.readObject());
                ObjectMapper mapper = new ObjectMapper();
                cinfo = mapper.readValue(recvStr,ChatInfoDTO.class);

                // 메시지 표시
                if(cinfo.getCommand()==Info.SEND){
                    System.out.println(cinfo.getMessage());
                }
                // 들어온 커맨드값이 SEND가 아닐경우 모든 클라이언트 퇴장 및 프로그램을 종료한다.
                else{
                    if (reader != null)
                        reader.close();
                    if (writer != null)
                        writer.close();
                    if (socket != null)
                        socket.close();
                    flag = false;
                }
            }catch(IOException e){
                System.out.println("서버로부터 정보를 수신중 오류가 발생했습니다.");
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }



}

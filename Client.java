import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;


    Client(Socket socket,String username){
        try{

            this.socket = socket;
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }catch (Exception e){
            closeEverything(socket,bw,br);
        }
    }

    public void sendMessage(){
        try{
            bw.write(username);
            bw.newLine();
            bw.flush();

            Scanner scan = new Scanner(System.in);
            while (socket.isConnected()){
                System.out.println("Client.sendMessage");
                String messageToSend = scan.nextLine();
                bw.write(username + " : " + messageToSend);
                bw.newLine();
                bw.flush();
            }
        }catch (Exception e){

        }
    }

    public void listenToMessage(){
        System.out.println("Client.listenToMessage");

       Thread trd = new Thread(new Runnable() {
           String messageFromGroupchat;
           @Override
           public void run() {
               try{
                   messageFromGroupchat = br.readLine();
                   System.out.println("Client.run");

                   while (messageFromGroupchat!=null){
                       System.out.println("message" + messageFromGroupchat);
                       messageFromGroupchat = br.readLine();


                   }

               }catch (IOException e){

               }

           }
       });
               trd.start();
    }

    public void  closeEverything(Socket socket,BufferedWriter bufferedWriter,BufferedReader bufferedReader){


        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if (socket != null){
                socket.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your username : ");
        String username = scan.next();
        Socket socket1 = new Socket("localhost",1234);

        Client client = new Client(socket1,username);
        client.sendMessage();
        client.listenToMessage();


    }
}

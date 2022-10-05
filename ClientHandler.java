import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    //Multi user representation
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    //To made a connection with the server
    private Socket socket;

    //read and write data

    private BufferedReader br;
    private BufferedWriter bw;

    String clientUsername ;
    public ClientHandler(Socket socket) {
        try {

            this.socket = socket;
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));//to send it in bytes

            this.clientUsername = br.readLine();
            clients.add(this);
            broadcastMessage("SERVER : " + clientUsername + "Has entered the group chat.");
        }catch (IOException exception){

        }

    }

    @Override
    public void run() {

         String messageFromClient;
         while (socket.isConnected()){
             try {

                 messageFromClient = br.readLine();

                 broadcastMessage(messageFromClient);
             }catch (Exception e){
                closeEverything(socket,bw,br);
             }
         }
    }

    public void broadcastMessage(String messageToSend){
        for (ClientHandler clientHandler : clients){
            try{
                if(!clientHandler.clientUsername.equals(this.clientUsername)){
                    clientHandler.bw.write(messageToSend);
                    clientHandler.bw.newLine();
                    clientHandler.bw.flush();
                }
            }catch (Exception e){
                closeEverything(socket,bw,br);
            }
        }
    }

    public void removeClientHandler(){
        clients.remove(this);
        broadcastMessage("SERVER: " + this.clientUsername + " has left the room");
    }

    public void  closeEverything(Socket socket,BufferedWriter bufferedWriter,BufferedReader bufferedReader){

        removeClientHandler();
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
}

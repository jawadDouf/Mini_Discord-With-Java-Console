import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;


public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private static Socket socket;

    private String clientUserName ;


    public ClientHandler(Socket socket) throws IOException {
        try{
            ClientHandler.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine();
            System.out.println(this.clientUserName);
            clients.add(this);
            broadcastMessage("Server :" + this.clientUserName + " has join the chat!!");
        }catch (IOException e)
        {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void broadcastMessage(String message) throws IOException {
         String[] messageContent = message.split(":");
         clients.stream().filter(client -> !client.clientUserName.equals(clientUserName)).forEach(
                 (client) -> {
                    try {
                        if(messageContent[0].equals("file")){
                            File destinationPath = new File("Document/"+System.currentTimeMillis()+messageContent[2]);
                            receiveFile(destinationPath.getPath());
                            client.bufferedWriter.write(messageContent[1]+" : file -> "+ destinationPath.toURI());
                        }else if (messageContent[0].equals("text")){
                            client.bufferedWriter.write(messageContent[1] + " : " + messageContent[2]);
                        }else{
                            client.bufferedWriter.write(message);
                        }
                         client.bufferedWriter.newLine();
                         client.bufferedWriter.flush();
                    }catch (IOException e){
                            closeEverything(socket,bufferedReader,bufferedWriter);
                    }

                }
        );
    }



    public static void receiveFile(String fileName) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        long size = dataInputStream.readLong();
        byte[] buffer = new byte[4*1024];

        while(size > 0 && (bytes = dataInputStream.read(buffer,0,(int)Math.min(buffer.length,size))) != -1){
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;
        }
        fileOutputStream.close();
    }


    public void clientLeft() throws IOException {
        clients.remove(this);
        broadcastMessage("Server: " + clientUserName + " has left the chat!!");
    }
    public void closeEverything(Socket socketPar,BufferedReader bufferedReaderPar,BufferedWriter bufferedWriterPar)  {
        try {
            clientLeft();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
             if (bufferedReaderPar != null) {
                 bufferedReaderPar.close();
             }
             if (bufferedWriterPar != null) {
                 bufferedWriterPar.close();
             }
             if (socketPar != null) {
                 socketPar.close();
             }
         }catch (IOException io){
             throw new RuntimeException(io);
         }


    }


    @Override
    public void run() {
        String messageClient ;
        while (socket.isConnected()){
            try{
                messageClient = bufferedReader.readLine();
                broadcastMessage(messageClient);
            }catch(IOException e){
                closeEverything(socket, bufferedReader,bufferedWriter);
                break;
            }
        }
    }
}
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }


    public void startedServer(){
        try {

            while (!serverSocket.isClosed()){
                //Wait till the user enterance then create a socket for this server.
                Socket socket = serverSocket.accept();
                System.out.println("A new Client is entered");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        }catch (Exception e){

        }
    }


    public static void main(String[] args) throws  IOException {
        ServerSocket serverSocket1 = new ServerSocket(1234);
        Server server = new Server(serverSocket1);
        server.startedServer();

    }
}

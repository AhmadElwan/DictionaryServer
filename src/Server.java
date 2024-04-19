import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

    private ServerSocket serverSocket ; // object responsible for listening to incoming connections and creating a socket object to communicate with them

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){

        try{

            while(!serverSocket.isClosed()){ // While the server socket is not closed
                Socket socket = serverSocket.accept(); // Accept a new connection
                System.out.println("New client connected, IP address : " + socket.getInetAddress().getHostAddress()); // Print the IP address of the client that connected to the server
                ClientHandler clientHandler = new ClientHandler(socket) ; // Create a new ClientHandler object
                Thread thread = new Thread(clientHandler); // Create a new Thread to run the ClientHandler object
                thread.start(); // Start the thread}

        }
    }  catch (IOException e) {



        }
}

    public void closeServerSocket(){

        try {
            // checks if there is a server socket and closes it
            if(serverSocket != null){
                serverSocket.close();
            }

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8080) ;
        Server server = new Server(serverSocket) ;
        server.startServer();

    }

}

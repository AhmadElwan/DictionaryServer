import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.net.Socket;

public class ClientHandler implements Runnable {

    // Array to keep track of all the clients
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader; // To read messages sent from the client
    private BufferedWriter bufferedWriter; // To send messages to the client
    private String clientUsername;

    // Constructor
    public ClientHandler(Socket socket) {


        try {

            this.socket = socket;
            this.bufferedReader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new java.io.OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this); // Add the new client to the array list of clients
            broadcastMessage("SERVER: " + clientUsername + " connected to the server!");

        } catch (IOException e) {

            closeEverything(socket, bufferedReader, bufferedWriter);

        }
    }


    @Override
    public void run() {

        String messageFromClient;

        while (socket.isConnected()) {

            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);

            } catch (IOException e) {

                closeEverything(socket, bufferedReader, bufferedWriter);
                break;

            }

        }
    }

    public void broadcastMessage(String messageToSend) {

        for (ClientHandler clientHandler : clientHandlers) {

            try {

                if (!clientHandler.clientUsername.equals(clientUsername)) {

                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();

                }

            } catch (IOException e) {

                closeEverything(socket, bufferedReader, bufferedWriter);

            }

        }
    }

    // Method to remove the client from the array list of clients
    public void removeClientHandler() {

        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " disconnected!");

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        removeClientHandler();
        try {
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

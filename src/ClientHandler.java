import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try ( // Use try-with-resources to ensure proper closing of streams
              Socket clientSocket = this.clientSocket;
              InputStream inputStream = clientSocket.getInputStream();
              InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
              BufferedReader reader = new BufferedReader(inputStreamReader);
              DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream())) {

            // Read the word sent by the client
            String word = reader.readLine();

            // Look up the word definition in the server's dictionary (access through static variable)
            String definition = Server.dictionary.get(word);

            System.out.println("Found word: " + word + (definition != null ? " (definition found)" : " (definition not found)"));

            // Construct response message
            String message = definition != null ? definition : "Word not found";

            // Send the response message back to the client using UTF-8 encoding
            outputStream.writeUTF(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
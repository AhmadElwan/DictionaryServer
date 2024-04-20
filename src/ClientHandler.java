import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * This class represents a ClientHandler object that handles communication
 * with an individual client connected to the server. It implements the
 * Runnable interface to enable execution in a separate thread.
 */
public class ClientHandler implements Runnable {

    private Socket clientSocket;

    /**
     * Constructor to create a ClientHandler object for a specific client socket.
     *
     * @param clientSocket The Socket object representing the connected client.
     */
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * This method implements the logic for handling a client connection.
     * It reads the word sent by the client, looks up the definition in the
     * server's dictionary (accessed through the static getDefinition method),
     * and sends the definition back to the client using UTF-8 encoding.
     */
    @Override
    public void run() {
        try ( // Use try-with-resources for automatic closing of streams
              Socket clientSocket = this.clientSocket;
              InputStream inputStream = clientSocket.getInputStream();
              InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
              BufferedReader reader = new BufferedReader(inputStreamReader);
              DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream())) {

            // Read the word sent by the client
            String word = reader.readLine();
            System.out.println("Client requested definition for: " + word); // Optional logging

            // Look up the word definition in the server's dictionary (static access)
            String definition = Server.getDefinition(word);

            // Prepare the response message (definition or "Word not found")
            String response = definition != null ? definition : "Word not found";

            // Send the response message back to the client using UTF-8 encoding
            outputStream.writeUTF(response);

        } catch (IOException e) {
            e.printStackTrace(); // Log or handle IOException appropriately
        }
    }
}

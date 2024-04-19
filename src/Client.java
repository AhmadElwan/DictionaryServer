import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class represents a client that can connect to a dictionary server
 * and look up word definitions.
 */
public class Client {

    // Define default server address and port
    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 8080;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    /**
     * Constructor to create a client object and connect to the server.
     *
     * @param serverAddress The server address to connect to.
     * @param port The port number to connect on.
     * @throws IOException If there's an error during connection or stream creation.
     */
    public Client(String serverAddress, int port) throws IOException {
        this.socket = new Socket(serverAddress, port); // Connect to server address and port
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        System.out.println("Connected to server!"); // Debugging message
    }

    /**
     * This method allows the user to enter words and sends them to the server
     * to look up their definitions. It continues until the user enters "quit".
     *
     * @throws IOException If there's an error sending or receiving messages.
     */
    public void sendMessage() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (socket.isConnected()) {
            System.out.print("Enter a word to look up (or 'quit' to exit): ");
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("quit")) {
                break;
            }
            System.out.println("Sending word: " + message); // Debugging message

            try {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                String response = bufferedReader.readLine();
                System.out.println("Meaning : " + response); // Debugging message
            } catch (IOException e) {
                System.err.println("Error sending or receiving message: " + e.getMessage());
                closeEverything();
                break;
            }
        }
    }

    /**
     * This method closes all open resources associated with the client,
     * including the socket, buffered reader, and buffered writer.
     *
     * @throws IOException If there's an error while closing any resource.
     */
    public void closeEverything() throws IOException {
        if (bufferedReader != null) {
            bufferedReader.close();
        }
        if (bufferedWriter != null) {
            bufferedWriter.close();
        }
        if (socket != null) {
            socket.close();
        }
    }

    /**
     * The main method is the entry point for the program. It parses arguments
     * (server address and port) and creates a Client object to initiate communication
     * with the server.
     *
     * @param args The command-line arguments.
     * @throws IOException If there's an error during client creation, communication,
     * or resource closing.
     */
    public static void main(String[] args) throws IOException {
        String serverAddress = DEFAULT_SERVER_ADDRESS;
        int port = DEFAULT_PORT;

        // Check if arguments are provided and handle them appropriately
        if (args.length > 0) {
            serverAddress = args[0]; // Override default server address if provided
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]); // Override default port if provided and valid
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number provided. Using default port: " + DEFAULT_PORT);
            }
        }

        Client client = new Client(serverAddress, port);
        client.sendMessage();
        //client.closeEverything(); // Close resources are closed automatically using a try-with-resources in the sendMessage method
    }
}
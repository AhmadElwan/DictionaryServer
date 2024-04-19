import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 8080;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(String serverAddress, int port) throws IOException {
        this.socket = new Socket(serverAddress, port); // Connect to server address and port
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        System.out.println("Connected to server!"); // Debugging message
    }

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
                System.out.println("Received response: " + response); // Debugging message
                System.out.println(response); // Print the server's response
            } catch (IOException e) {
                System.err.println("Error sending or receiving message: " + e.getMessage());
                closeEverything();
                break;
            }
        }
    }

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

    public static void main(String[] args) throws IOException {
        String serverAddress = DEFAULT_SERVER_ADDRESS;
        int port = DEFAULT_PORT;

        // Check if arguments are provided
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
        //client.closeEverything();
    }
}
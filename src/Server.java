import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    private ServerSocket serverSocket;
    static HashMap<String, String> dictionary; // Static variable to store the dictionary

    /**
     * Constructor that creates a server socket on a specified port and reads the dictionary from an external file.
     * @param port The port number on which the server will listen for connections.
     * @param dictionaryFilePath The path to the dictionary file containing word-meaning pairs.
     * @throws IOException If there is an error creating the server socket or reading the dictionary file.
     */
    public Server(int port, String dictionaryFilePath) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.dictionary = readDictionaryFromFile(dictionaryFilePath);
    }


    /**
     * Reads the dictionary file and populates a HashMap with word-meaning pairs.
     * @param filePath The path to the dictionary file.
     * @return A HashMap containing word-meaning pairs.
     * @throws IOException If there is an error reading the dictionary file.
     */
    private HashMap<String, String> readDictionaryFromFile(String filePath) throws IOException {
        HashMap<String, String> dictionary = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    dictionary.put(parts[0].trim(), parts[1].trim()); // Trim leading/trailing spaces
                } else {
                    System.err.println("Invalid line format in dictionary file: " + line);
                }
            }
        }
        System.out.println("Dictionary loaded successfully." + dictionary);
        System.out.println("Dictionary size: " + dictionary.size());
        return dictionary;
    }

    /**
     * Starts the server, listens for incoming connections, and creates separate threads
     * to handle each client concurrently.
     * @throws IOException If there is an error during communication with the socket.
     */
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected, IP address : " + socket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        int port = 8080;
        String dictionaryFile = "C:\\Users\\ahmadelwan\\Desktop\\Uni\\Distributed\\Assignment1\\Multi-threaded_Dictionary_Server\\src\\Dictionary.txt";
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number provided. Using default port: " + port);
            }
        }
        if (args.length > 1) {
            dictionaryFile = args[1];
        }

        Server server = new Server(port, dictionaryFile);
        server.startServer();
    }
}
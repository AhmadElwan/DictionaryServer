import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * This class represents a server that can load a dictionary from a file
 * and respond to client requests for word definitions.
 */
public class Server {

    private ServerSocket serverSocket;

    /**
     * A HashMap to store word definitions loaded from the dictionary file.
     * Declared as static to ensure a single instance shared across all threads.
     */
    private static HashMap<String, String> dictionary;

    /**
     * Constructor to create a Server object, specifying the port number
     * and the path to the dictionary file.
     *
     * @param port The port number to listen on for client connections.
     * @param dictionaryFilePath The path to the text file containing word definitions.
     * @throws IOException If there's an error opening the server socket or reading the dictionary file.
     */
    public Server(int port, String dictionaryFilePath) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.dictionary = readDictionaryFromFile(dictionaryFilePath); // Load dictionary on server startup
    }

    /**
     * This method reads word definitions from a text file and stores them in a HashMap.
     *
     * @param filePath The path to the text file containing word definitions.
     * @return A HashMap containing word-definition pairs loaded from the file.
     * @throws IOException If there's an error reading the dictionary file.
     */
    private HashMap<String, String> readDictionaryFromFile(String filePath) throws IOException {
        HashMap<String, String> dictionary = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    dictionary.put(parts[0].trim(), parts[1].trim()); // Add word-definition pair (trimmed)
                } else {
                    System.err.println("Invalid line format in dictionary file: " + line);
                }
            }
        }
        System.out.println("Dictionary loaded successfully.");
        return dictionary;
    }

    /**
     * This method starts the server, listens for incoming client connections,
     * and creates a new thread for each client to handle requests concurrently.
     *
     * @throws IOException If there's an error accepting client connections.
     */
    public void startServer() throws IOException {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept(); // Wait for and accept a client connection
                System.out.println("New client connected, IP address : " + socket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(socket); // Create a client handler object
                Thread thread = new Thread(clientHandler);
                thread.start(); // Start a new thread to handle the client connection
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter method to access the shared dictionary HashMap
     * (synchronized to ensure thread safety when multiple threads access it).
     *
     * @param word The word for which the definition is requested.
     * @return The definition of the word if found in the dictionary, null otherwise.
     */
    public static synchronized String getDefinition(String word) {
        return dictionary.get(word);
    }

    /**
     * The main method is the entry point for the program. It parses arguments
     * (port number and dictionary file path) and creates a Server object to initiate
     * the server and start listening for client connections.
     *
     * @param args The command-line arguments.
     * @throws IOException If there's an error during server creation or startup.
     */
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

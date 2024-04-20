import javax.swing.*; // Import libraries for Swing GUI components

import java.awt.*;  // Import libraries for layout managers
import java.awt.event.*; // Import libraries for action listeners

import java.io.BufferedReader; // Import libraries for reading data from socket
import java.io.BufferedWriter; // Import libraries for writing data to socket
import java.io.IOException; // Import library for handling IO exceptions
import java.io.InputStreamReader; // Import library for converting socket input stream to reader
import java.io.OutputStreamWriter; // Import library for converting socket output stream to writer
import java.net.Socket; // Import library for creating sockets

/**
 * This class represents a graphical user interface (GUI) client application
 * that can connect to a dictionary server, send word look-up requests,
 * and receive word definitions. It inherits from JFrame and implements ActionListener.
 */
public class ClientGUI extends JFrame implements ActionListener {

    private static final String DEFAULT_SERVER_ADDRESS = "localhost"; // Default server address
    private static final int DEFAULT_PORT = 8080; // Default server port

    private Socket socket; // Socket for network communication
    private BufferedReader bufferedReader; // Reader for receiving messages from server
    private BufferedWriter bufferedWriter; // Writer for sending messages to server

    private JTextField textField; // Text field for user input (word to look up)
    private JTextArea textArea; // Text area to display conversation history

    /**
     * Constructor to create a ClientGUI object, connect to the server,
     * and initialize the GUI components.
     *
     * @param serverAddress The server address to connect to.
     * @param port The port number to connect on.
     * @throws IOException If there's an error during connection or stream creation.
     */
    public ClientGUI(String serverAddress, int port) throws IOException {
        this.socket = new Socket(serverAddress, port); // Connect to server
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        System.out.println("Connected to server!");

        setTitle("Dictionary Client"); // Set window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set close operation
        setSize(400, 300); // Set window size
        setLayout(new BorderLayout()); // Set layout manager

        textField = new JTextField("Enter word here...", 20); // Create text field with initial text and size
        add(textField, BorderLayout.NORTH); // Add text field to north section of border layout

        textArea = new JTextArea(); // Create text area
        textArea.setEditable(false); // Set text area to non-editable
        add(new JScrollPane(textArea), BorderLayout.CENTER); // Add text area with scroll pane to center section

        JButton sendButton = new JButton("Send"); // Create send button
        sendButton.addActionListener(this); // Add action listener to the button
        add(sendButton, BorderLayout.SOUTH); // Add send button to south section

        setVisible(true); // Make the window visible
    }

    /**
     * This method is invoked when an action event occurs (e.g., button click).
     * In this case, it handles the "Send" button click by sending the entered word
     * to the server, receiving the response, and updating the text area.
     *
     * @param e The ActionEvent object containing information about the event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Send")) {
            String message = textField.getText(); // Get the word from the text field
            if (!message.isEmpty()) {
                try {
                    bufferedWriter.write(message); // Write the word to the server
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    String response = bufferedReader.readLine(); // Read the response from the server
                    textArea.append("You: " + message + "\n"); // Append sent word to text area
                    textArea.append("Server: " + (response != null ? response : "Word not found !!") + "\n"); // Append response to text area
                    textField.setText(""); // Clear the text field for next input
                } catch (IOException ex) {
                    System.err.println("Error sending or receiving message: " + ex.getMessage());
                    closeEverything(); // Close resources if there's an error
                }
            }
        }
    }

    /**
     * This method closes all open resources associated with the client,
     * including the socket, buffered reader, and buffered writer.
     *
     * @throws IOException If there's an error while closing any resource.
     */
    public void closeEverything() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close(); // Close the buffered reader
            }
            if (bufferedWriter != null) {
                bufferedWriter.close(); // Close the buffered writer
            }
            if (socket != null) {
                socket.close(); // Close the socket
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print the exception stack trace if an error occurs
        }
    }

    public static void main(String[] args) {
        String serverAddress;
        int port = DEFAULT_PORT;

        if (args.length > 0) {
            serverAddress = args[0]; // Override default server address if provided
        } else {
            serverAddress = DEFAULT_SERVER_ADDRESS;
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]); // Override default port if provided and valid
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number provided. Using default port: " + DEFAULT_PORT);
            }
        }

        int finalPort = port;
        SwingUtilities.invokeLater(() -> {
            try {
                new ClientGUI(serverAddress, finalPort);
            } catch (IOException e) {
                System.err.println("Error connecting to the server: " + e.getMessage());
            }
        });
    }
    }
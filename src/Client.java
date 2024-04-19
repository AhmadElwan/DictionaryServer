import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket ;
    private BufferedReader bufferedReader ;
    private BufferedWriter bufferedWriter ;
    private String username ;

    public Client(Socket socket, String username) {

        try {

            this.socket = socket ;
            this.bufferedReader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new java.io.OutputStreamWriter(socket.getOutputStream()));
            this.username = username;

        } catch (IOException e) {

            closeEverything(socket, bufferedReader, bufferedWriter);

        }

    }

    public void sendMessage() {

        try{

            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while(socket.isConnected()) {

                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + " : " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }

        } catch (IOException e) {

            closeEverything(socket, bufferedReader, bufferedWriter);

        }
    }

    public void listenForMessage() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String messageFromClient;

                while(socket.isConnected()) {


                    try {

                        messageFromClient = bufferedReader.readLine();
                        System.out.println(messageFromClient);

                    } catch (IOException e) {

                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break;

                    }
                }

            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

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

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username : ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 8080);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();

    }


}

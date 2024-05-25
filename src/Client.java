import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Scanner scan = new Scanner(System.in);
             Socket client = new Socket("127.0.0.1", 8000);
             PrintWriter out = new PrintWriter(client.getOutputStream(), true);//for sending message to connection handler
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) { //for receiving message from connection handler

            ReceiveMessageController receiveMessageController = new ReceiveMessageController(in, client);
            receiveMessageController.start();

            System.out.println("\t\t\tWelcome to the chatroom!");
            System.out.println("Enter your name: ");

            while (!client.isClosed()) {
                out.println(scan.nextLine());
            }
            System.out.println("Bye!");
        } catch (IOException e) {
            System.err.println("Problem with reading from client and sending it to connection handler!");
            Server.shutdown();
        }
    }
}

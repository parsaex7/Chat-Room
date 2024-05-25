import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ReceiveMessageController extends Thread {
    private final BufferedReader in; //read from connection handler
    private final Socket client;

    public ReceiveMessageController(BufferedReader in, Socket client) {
        this.in = in;
        this.client = client;
    }

    public void run() {
        try {
            String message;
            while (!client.isClosed() && (message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            System.err.println("Problem with reading message in client -> receiveMessageController");
        }
    }
}

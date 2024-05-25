import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionHandler implements Runnable {
    public static final List<ConnectionHandler> handlers = new ArrayList<>();
    private final Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String nickName;
    private boolean isShutDowm =false;

    public ConnectionHandler(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream())); //for sending message to client
            out = new PrintWriter(client.getOutputStream(), true); //for receiving message to client

            nickName = in.readLine();
            broadcast(nickName + " joined!");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/quit")) {
                    broadcast(nickName + " left the chat!");
                    shutdown();
                    break;
                } else if (message.startsWith("/nick ")) {
                    String[] details = message.split(" ", 2);
                    if (details.length == 2) {
                        String newNick = details[1];
                        broadcast(nickName + " changed nickname to " + newNick);
                        nickName = newNick;
                    } else {
                        out.println("Invalid syntax! (/nick 'new Nickname')");
                    }
                } else {
                    broadcast(nickName + ": " + message);
                }
            }
        } catch (IOException e) {
            try {
                shutdown();
            } catch (IOException ex) {
                System.err.println("problem in connection handler for  shutdown!");
            }
        } finally {
            try {
                if (!isShutDowm) {
                    shutdown();
                }
            } catch (IOException e) {
                System.err.println("problem in connection handler for  shutdown!");
            }
        }
    }

    public void shutdown() throws IOException {
        broadcast(nickName + " Leave the chat!");
        isShutDowm = true;
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        if (!client.isClosed()) {
            client.close();
        }
    }

    private void broadcast(String message) {
        synchronized (handlers) {
            for (ConnectionHandler handler : handlers) {
                if (handler != this) {
                    handler.out.println(message);
                }
            }
        }
    }
}

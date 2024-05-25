import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static ServerSocket serverSocket;
    private final ExecutorService pool;

    public Server() throws IOException {
        serverSocket = new ServerSocket(8000);
        pool = Executors.newCachedThreadPool();
    }

    public static void shutdown() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            synchronized (ConnectionHandler.handlers) {
                for (ConnectionHandler ch : ConnectionHandler.handlers) {
                    ch.shutdown();
                }
            }
            System.out.println("Server shutDown correctly!");
        } catch (IOException e) {
            System.err.println("Problem with closing everything!");
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            System.out.println("Server created.\nwait for new client");
            while (true) {
                Socket client = serverSocket.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                synchronized (ConnectionHandler.handlers) {
                    ConnectionHandler.handlers.add(handler);
                }
                server.pool.execute(handler);
                System.out.println("Number of Active User : " + ConnectionHandler.handlers.size());
            }
        } catch (IOException e) {
            shutdown();
            System.err.println("Problem with closing server!");
        }
    }
}

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server  extends Thread{
    private ServerSocket serverSocket;
    private Pair<Integer[], Double[]>  lv;
    public int id;

    public Server(ServerSocket serverSocket, int id){
        this.serverSocket = serverSocket;
        this.id = id;
        this.lv = null;
    }

    @Override
    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
                lv = clientHandler.get_lv_recieved();
                while (lv == null){
                    lv = clientHandler.get_lv_recieved();
                }
                if (lv != null)
                    System.out.println("Server got message on port: " + socket.getLocalPort() + " message is: [" + lv.getValue()[0].intValue() + ", "+  lv.getValue()[1].intValue()+ ", "+  lv.getValue()[2].intValue() +"]");
            }
        } catch (IOException e) {
        }
    }
    public Pair<Integer[], Double[]>  getLv() {return lv;}
}
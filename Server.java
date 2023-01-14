import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server  extends Thread{
    private ServerSocket serverSocket;
    private Pair<Integer[], Double[]>  lv;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    public int id;

    public Server(ServerSocket serverSocket, int id){
        this.serverSocket = serverSocket;
        this.id = id;
        this.lv = null;
    }

    @Override
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                this.objectInputStream = new ObjectInputStream(socket.getInputStream());
                Object o = objectInputStream.readObject();
                Pair<Integer[], Double[]> temp = (Pair<Integer[], Double[]>) o;
                if (temp != null) {
                    lv = temp;
                    System.out.println("Server: "+id+" Got message: " + lv.getKey()[0] + " -> "+ lv.getKey()[1]);
                }
            } catch(IOException | ClassNotFoundException e) { }
        }
        System.out.println("server socket "+ id + "has closed");
    }
    public Pair<Integer[], Double[]>  getLv() {return lv;}

    public void closeEverything() {
        try{
            this.serverSocket.close();
        } catch (IOException e){}
    }

}
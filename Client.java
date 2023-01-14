import java.net.*;
import java.util.*;
import java.io.*;

public class Client{
//    public class Client extends Thread{

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private int id;


    public Client(Socket socket, int id){
        this.socket = socket;
        this.id = id;
        try {
            OutputStream outputStream = socket.getOutputStream();
            this.objectOutputStream = new ObjectOutputStream(outputStream);
            InputStream inputStream = socket.getInputStream();
            this.objectInputStream = new ObjectInputStream(inputStream);
        } catch (IOException e){ }

    }

    public void sendMessage(Pair<Integer[], Double[]> lv_to_send){
        try {
            if(socket.isConnected() && objectOutputStream != null) {
                objectOutputStream.writeObject(lv_to_send);
                objectOutputStream.flush();
//                System.out.println("Client: " +lv_to_send.getKey()[0] + " -> "+ lv_to_send.getKey()[1]);
            }
        } catch (IOException e){
            closeEverything();
        }
    }

    public void closeEverything() {
        try {
            if (this.socket != null) {
                this.socket.close();
            }
            if (this.objectOutputStream != null) {
                this.objectOutputStream.close();
            }
        } catch (IOException e) {}
    }
}

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
//                System.out.println("SENT message to port: " + socket.getPort() + " message is: [" + lv_to_send.getValue()[0].intValue() + ", "+  lv_to_send.getValue()[1].intValue()+", " + lv_to_send.getValue()[2].intValue()+"]");
                objectOutputStream.writeObject(lv_to_send);
                objectOutputStream.flush();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import java.net.*;
import java.util.*;
import java.io.*;

public class Client{
//    public class Client extends Thread{

    private Socket socket;
    private Socket get_socket;
    public Pair<Integer[], Double[]> lv_to_return;
    private ObjectOutputStream objectOutputStream;
    private int id;


    public Client(Socket socket, int id){
        this.socket = socket;
        this.get_socket = get_socket;
        this.id = id;
        try {
            OutputStream outputStream = socket.getOutputStream();
            this.objectOutputStream = new ObjectOutputStream(outputStream);
        } catch (IOException e){ }

    }

    public void sendMessage(Pair<Integer[], Double[]> lv_to_send){
        try {
            if(socket.isConnected() && objectOutputStream != null) {
//                System.out.println("SENT message to port: " + socket.getPort() + " message is: [" + lv_to_send.getValue()[0].intValue() + ", "+  lv_to_send.getValue()[1].intValue()+", " + lv_to_send.getValue()[2].intValue()+"]");
                if (!(lv_to_send instanceof Pair))
                    System.out.println("");
                objectOutputStream.writeObject(lv_to_send);
                objectOutputStream.flush();
            }
        } catch (IOException e){
            closeEverything(socket, objectOutputStream);
        }
    }

private void closeEverything(Socket socket, ObjectOutputStream objectOutputStream) {
    try {
        if (socket != null) {
            socket.close();
        }
        if (objectOutputStream != null) {
            objectOutputStream.close();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

//    @Override
//    public void run(){
//        listenToMessage();
//    }
}

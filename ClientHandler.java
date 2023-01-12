import java.net.*;
import java.util.*;
import java.io.*;

public class ClientHandler implements Runnable{

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private Pair<Integer[], Double[]> lv_to_send;
    private Pair<Integer[], Double[]> lv_recieved;

    public ClientHandler(Socket socket){
        lv_recieved = null;
        try{
            this.socket = socket;
            OutputStream outputStream = socket.getOutputStream();
            this.objectOutputStream = new ObjectOutputStream(outputStream);
            InputStream inputStream = socket.getInputStream();
            this.objectInputStream = new ObjectInputStream(inputStream);
        }catch (IOException e){
            closeEverything();
        }

    }

//    public void set_lv_to_send(Pair<Integer[], Double[]> lv){ this.lv_to_send = lv;}
    public Pair<Integer[], Double[]> get_lv_recieved(){ return lv_recieved;}

    @Override
    public void run(){
        Pair<Integer[], Double[]> lv;
        while(socket.isConnected()){
            try {
                Object o = objectInputStream.readObject();
                if (!(o instanceof Pair))
                    System.out.println();
                Pair<Integer[], Double[]> temp = (Pair<Integer[], Double[]>) o;
                if (temp != null)
                    lv_recieved = temp;
//                objectOutputStream.writeObject(lv_recieved);
//                objectOutputStream.flush();

//                System.out.println("Server got message on port: " + socket.getLocalPort() + " message is: [" + lv_recieved.getValue()[0].intValue() + ", "+  lv_recieved.getValue()[1].intValue()+ ", "+  lv_recieved.getValue()[2].intValue() +"]");
            } catch(IOException | ClassNotFoundException e) {
            }
        }
    }

    public void sendMessage(Pair<Integer[], Double[]> lv_to_send){
        try {
            objectOutputStream.writeObject(lv_to_send);
            objectOutputStream.flush();
        } catch (IOException e) {}
    }

    public void closeEverything(){
//        removeClientHandler();
        try{
            if (this.objectInputStream != null)
                this.objectInputStream.close();
            if (this.objectOutputStream != null)
                this.objectOutputStream.close();
            if (this.socket != null)
                this.socket.close();
        } catch (IOException e) {
        }
    }
}

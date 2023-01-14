import java.net.*;
import java.io.*;
import java.util.*;

public class Server  extends Thread{
    private ServerSocket serverSocket;
    public ArrayList<Pair<Integer, Double[]>>  lvs;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    public int id;
    private int num_of_nodes;

    public Server(ServerSocket serverSocket, int id, int num_of_nodes){
        this.serverSocket = serverSocket;
        this.id = id;
        this.num_of_nodes = num_of_nodes;
        this.lvs = new ArrayList<>();
    }

    @Override
    public void run() {
//        Map<Integer, Double[]> values_got_current_round = new HashMap<Integer, Double[]>();
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                this.objectInputStream = new ObjectInputStream(socket.getInputStream());
                while(socket.isConnected()){
                    Object o = objectInputStream.readObject();
                    Pair<Integer, Double[]> temp = (Pair<Integer, Double[]>) o;

                    if (temp != null) {
                        if(temp.getKey() == id)
                            continue;
                        if(lvs.size() == 0) {
                            lvs.add(temp);
                        }
                        if(lvs.size() == num_of_nodes)
//                            todo: memory problem too many element in lvs I think.
                            remodeDuplicates();
                        for(int i = 0; i < lvs.size(); i++){
                            if(lvs.get(i).getKey().intValue() == temp.getKey().intValue()) {
                                lvs.set(i, temp);
                            }
                            else {
                                lvs.add(temp);
                            }
                        }
                    }
                }
            } catch(IOException | ClassNotFoundException e) { }
        }
//        System.out.println("server socket "+ id + "has closed");
    }
    public ArrayList<Pair<Integer, Double[]>>  getLvs() {return lvs;}

    public void remodeDuplicates(){

    }

    public void closeEverything() {
        try{
            this.serverSocket.close();
        } catch (IOException e){}
    }

}
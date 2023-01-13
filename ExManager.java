import java.io.*;
import java.net.*;
import java.util.*;

public class ExManager {
    private String path;
    private int num_of_nodes;
    private Scanner initial_data;
    private Node[] network;
    private Server[] servers;

    public ExManager(String path) throws FileNotFoundException {
        this.path = path;
        initial_data = new Scanner(new File(path));
    }

    //    public Node getNode(int id){return network[id];}
    public Node get_node(int i) {
        return network[i - 1];
    }

    public int getNum_of_nodes() {
        return this.num_of_nodes;
    }

    public void update_edge(int id1, int id2, double weight) {
        network[id1 - 1].edge_update(id2, weight);
        network[id2 - 1].edge_update(id1, weight);
    }

    public void read_txt() {
        num_of_nodes = Integer.parseInt(initial_data.nextLine());
        this.network = new Node[num_of_nodes];
        this.servers = new Server[2*num_of_nodes];
        String line;
        while (initial_data.hasNextLine()) {
            line = initial_data.nextLine();
            if (line.equals("stop"))
                break;
            Node n = new Node(num_of_nodes, line);
            network[n.get_id() - 1] = n;
        }
    }

//  link state routing
    public void start() {
        for (int i = 0; i < network.length; i++) {
            Thread thread = new Thread(network[i]);
            thread.start();
        }
//        System.out.println();
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {e.printStackTrace();}

//        for (int i = 0; i < network.length; i++) { network[i].clients_initialize();}
//
////        ArrayList<Thread> threads = new ArrayList<Thread>();
//        ArrayList<Boolean> status = new ArrayList<>(network.length);
//        for (int i = 0; i < network.length; i++) {
//            status.add(false);
//            Thread thread = new Thread(network[i]);
//            thread.start();
////            threads.add(thread);
//        }
//        for (int i = 0; i < network.length; i++){
//            if (network[i].finished)
//                status.add(i, true);
//        }
//
//        while (status.contains(false)){
////            waits
//            for (int i = 0; i < network.length; i++) {
//                if (network[i].finished)
//                    status.add(i, true);
//            }
////            System.out.print("f");
//        }
////        System.out.println();
////        for (int i = 0; i < network.length; i++) {
////            try{
////                threads.get(i).join();
////            } catch (InterruptedException e){
////                e.printStackTrace();
////            }
////        }
//        System.out.println();
    }
}

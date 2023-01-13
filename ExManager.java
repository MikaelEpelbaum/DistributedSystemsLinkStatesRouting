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
    public void terminate(){
        for (int i = 0; i < network.length; i++) {
            network[i].killClients();
        }
        for (int i = 0; i < network.length; i++) {
            network[i].killServers();
        }
    }

//  link state routing
    public void start() {
        ArrayList<Thread> threads = new ArrayList<>(num_of_nodes);
        for (int i = 0; i < num_of_nodes; i++) {
            Thread thread = new Thread(network[i]);
            threads.add(thread);
            thread.start();
        }
        for (int i = 0; i < num_of_nodes; i++) {
            try {
                threads.get(i).join(50);
            } catch (InterruptedException e) {}
        }
        threads = null;
    }
}

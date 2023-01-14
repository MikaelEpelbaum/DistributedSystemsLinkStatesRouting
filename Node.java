import java.net.*;
import java.util.*;
import java.io.*;

public class Node extends Thread {
    private int id;
    public ArrayList<Pair> neighbors;
    public ArrayList<Pair> servers;
    public ArrayList<Pair<Integer, Client>> clients;
    public int num_of_nodes;
    public Double[][] matrix;
    public boolean finished;

    /**
     * Constructor
     *
     * @param num_of_nodes
     * @param line         initialize num_of_nodes and neighbors matrix
     *                     calling update_matrix function
     */
    public Node(int num_of_nodes, String line) {
        finished = false;
        this.num_of_nodes = num_of_nodes;
        matrix = new Double[num_of_nodes][num_of_nodes];
        for (int i = 0; i < num_of_nodes; i++) {
            for (int j = 0; j < num_of_nodes; j++) {
                matrix[i][j] = -1.0;
            }
        }
        neighbors_initialize(line);
        servers = new ArrayList<>(neighbors.size());
        clients = new ArrayList<>(neighbors.size());
        servers_initialize();
    }

    public int get_id() {
        return this.id;
    }

    /**
     * @param line Creates Pairs object
     *             updates neighbor list with new neighbor
     *             updates neighbors matrix of node
     */
    public void neighbors_initialize(String line) {
        String[] orders = line.split(" ");
        neighbors = new ArrayList<>((orders.length - 1) / 4);
        this.id = Integer.parseInt(orders[0]);
        for (int i = 1; i < orders.length; i += 4) {
            Map<String, Double> neighbor_data = new HashMap<>();
            neighbor_data.put("weight", Double.parseDouble(orders[i + 1]));
            neighbor_data.put("send", Double.parseDouble(orders[i + 2]));
            neighbor_data.put("listen", Double.parseDouble(orders[i + 3]));

            Pair<Integer, Map> edge = new Pair<>(Integer.parseInt(orders[i]), neighbor_data);
            neighbors.add(edge);
            update_matrix(id, edge.getKey().intValue(), neighbor_data.get("weight"));
        }
    }

    private void servers_initialize() {
        for(Pair<Integer, Map> neighbor : neighbors){
            try {
                int port = ((Double) neighbor.getValue().get("listen")).intValue();
                ServerSocket serverSocket = new ServerSocket(port);
                Server s = new Server(serverSocket, id);
                Thread t = new Thread(s);
                t.start();
                servers.add(new Pair(neighbor.getKey(), s));
            } catch (IOException e){
            }
        }
//        System.out.println(id +" servers have been initialised");
    }

    public void clients_initialize(){
        for(Pair<Integer, Map> neighbor : neighbors){
            try {
                int port = ((Double) neighbor.getValue().get("send")).intValue();
                Socket socket = new Socket("localhost", port);
                Client c = new Client(socket, id);
                clients.add(new Pair(neighbor.getKey(), c));
            } catch (IOException e){
            }
        }
//        System.out.println(id + " clients have been initialised");
    }

// 4 1 8.9 13821 6060 2 1.0 17757 28236 5 1.5 1603 24233 3 6.6 27781 1213
//    key: 1
//    value: dictionary = {weight: 8.9,
//                        port_origin: 13821,
//                        port_dest: 6060}


    public void update_matrix(int i, int j, Double weight) {
        this.matrix[i - 1][j - 1] = weight;
    }

    public void edge_update(int id, double weight) {
        Integer temp = id;
        for (int i = 0; i < neighbors.size(); i++) {
            if (neighbors.get(i).getKey() == temp) {
                Map<String, Double> map = (Map<String, Double>) neighbors.get(i).getValue();
                map.put("weight", weight);
                update_matrix(this.id, id, weight);
            }
        }
    }

    public void sendMessage(Pair<Integer[], Double[]> lv) {
//      broadcast message to neighbors (clients)
        for(int i = 0; i < clients.size(); i++){
            Integer[] key = new Integer[]{lv.getKey()[0], clients.get(i).getKey()};
            Pair<Integer[], Double[]> loc = new Pair<>(key,(Double[]) lv.getValue());
            clients.get(i).getValue().sendMessage(loc);
        }
    }

    public ArrayList<Pair> getMessages(){
        HashMap<Integer, Pair> neighbors_lv = new HashMap<Integer, Pair>();
        while(neighbors_lv.size() < neighbors.size()){
            for(int i = 0; i < servers.size(); i ++){
                Pair<Integer[], Double[]> lv = ((Server)servers.get(i).getValue()).getLv();
                if (lv != null) {
                    if (lv.getKey()[0].intValue() == id)
                        continue;
                    Pair<Integer, Map> temp_neighbor = neighbors.get(i);
                    neighbors_lv.put(temp_neighbor.getKey().intValue(), lv);
                }
            }
        }
        Collection<Pair> values = neighbors_lv.values();
        ArrayList<Pair> r = new ArrayList<>(values);
        return r;
    }


    @Override
    public void run(){
        clients_initialize();
        Map<Integer, Boolean> updated = new HashMap<>();
        for (int i = 0; i < num_of_nodes+1; i++)
            updated.put(i, false);
        updated.put(id, true);
        updated.put(0, true);

        Pair<Integer[], Double[]> lv = new Pair<>(new Integer[]{id, 0}, matrix[id - 1]);
        sendMessage(lv);


        while (updated.containsValue(false)) {
            ArrayList<Pair> responses = getMessages();
            for (Pair lv_response : responses) {
                Pair<Integer[], Double[]> response = (Pair<Integer[], Double[]>) lv_response;
                int message_origin_id = response.getKey()[0].intValue();

                if (!updated.get(message_origin_id)) {
                    Double[] vec = response.getValue();
                    for (int i = 0; i < num_of_nodes; i++)
                        update_matrix(message_origin_id, i + 1, vec[i]);
                    updated.put(message_origin_id, true);

                    sendMessage(lv_response);
                }
            }
        }
        for (Pair<Integer, Client> c : clients){
            c.getValue().closeEverything();
        }
        finished = true;
    }

    public void killClients() {
        for (Pair p : clients) {
            ((Client) p.getValue()).closeEverything();
        }
    }
    public void killServers(){
        for(Pair p: servers) {
            ((Server)p.getValue()).closeEverything();
        }
    }

        public void print_graph () {
            for(int i = 0; i<num_of_nodes; i++){
                for(int j = 0; j<num_of_nodes; j++)
                {
                    System.out.print(matrix[i][j]);
                    if (j < num_of_nodes -1)
                        System.out.print(", ");
                }
                System.out.println();
            }
        }

}

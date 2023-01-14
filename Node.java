import java.net.*;
import java.util.*;
import java.io.*;

public class Node extends Thread {
    private int id;
    public ArrayList<Pair> neighbors;
    public ArrayList<Pair> servers;
    public ArrayList<Thread> serversThreads;
    public ArrayList<Pair<Integer, Client>> clients;
    public int num_of_nodes;
    public Double[][] matrix;
    public static boolean staticFinished = false;
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
        servers = new ArrayList<>();
        serversThreads = new ArrayList<>();
        for(Pair<Integer, Map> neighbor : neighbors){
            try {
                int port = ((Double) neighbor.getValue().get("listen")).intValue();
                ServerSocket serverSocket = new ServerSocket(port);
                Server s = new Server(serverSocket, id, num_of_nodes);
                Thread t = new Thread(s);
                t.start();
                serversThreads.add(t);
                servers.add(new Pair(neighbor.getKey(), s));
            } catch (IOException e){
            }
        }
    }

    public void cleanServers(){
        for(int i = 0; i <servers.size(); i++){
            ((Server)servers.get(i).getValue()).lvs = new ArrayList<>();
        }
    }

    public void clients_initialize(){
        clients = new ArrayList<>();
        for(Pair<Integer, Map> neighbor : neighbors){
            try {
                int port = ((Double) neighbor.getValue().get("send")).intValue();
                Socket socket = new Socket("localhost", port);
                Client c = new Client(socket, id);
                clients.add(new Pair(neighbor.getKey(), c));
            } catch (IOException e){
            }
        }
    }

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

    public void sendMessage(Pair<Integer, Double[]> lv) {
//      broadcast message to neighbors (clients)
        for(int i = 0; i < clients.size(); i++){
            clients.get(i).getValue().sendMessage(lv);
        }
    }

    public Map<Integer, Double[]> getMessages(){
        ArrayList<Pair> r = new ArrayList<>();
        for(int i = 0; i < servers.size(); i ++){
            r.addAll(((Server)servers.get(i).getValue()).getLvs());
        }
        Map<Integer, Double[]> m = new HashMap<>();
        for (Pair<Integer, Double[]> p: r) {
            m.put(p.getKey(), p.getValue());
        }
        return m;
//        Map<Integer, Double[]> r = new HashMap();
//        for(int i = 0; i < servers.size(); i ++) {
//            Map<Integer, Double[]> temp = ((Server) servers.get(i).getValue()).getLvs();
//            if(temp != null){
//                for(int j = 0; j <temp.size(); j++){
//                    r.put(temp., temp.get(origin_id));
//                }
//
//
//                try{
//                for (Integer origin_id : temp.keySet()) {
////                    if (r.containsKey(origin_id))
////                        continue;
//                    r.put(origin_id, temp.get(origin_id));
//                    }
//                } catch (ConcurrentModificationException e){
//                        System.out.println(e);
//                }
//            }
//        }
//        return r;
    }


    @Override
    public void run(){
        clients_initialize();
        Map<Integer, Boolean> updated = new HashMap<>();
        for (int i = 0; i < num_of_nodes+1; i++)
            updated.put(i, false);
        updated.put(id, true);
        updated.put(0, true);

        Pair<Integer, Double[]> lv = new Pair<>(id, matrix[id - 1]);
//        System.out.println("Initial messages id = " + id + " vec: "+ Arrays.toString(lv.getValue()));
        sendMessage(lv);

        while (updated.containsValue(false) && !finished && !staticFinished) {
            Map<Integer, Double[]> responses = getMessages();
            for (Integer message_origin_id: responses.keySet()) {
                if (!updated.get(message_origin_id)) {
                    Double[] vec = responses.get(message_origin_id);
                    for (int i = 0; i < num_of_nodes; i++)
                        update_matrix(message_origin_id, i + 1, vec[i]);
                    updated.put(message_origin_id, true);
//                    System.out.println("node: " + id + " has got a message from: " + message_origin_id);
                    sendMessage(new Pair(message_origin_id, vec));
                }
            }
            if(!updated.containsValue(false)) {
                finished = true;
            }
        }
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

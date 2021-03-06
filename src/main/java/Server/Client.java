package Server;

import inf112.skeleton.app.RoboRallyDemo;
import inf112.skeleton.app.mainMenu;
import javax.swing.JFrame;

public class Client extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;

    private Thread run, listen;
    private ClientBackend client;
    private boolean ready = false;
    private String orde;
    private String move;
    private int[] order;
    private String[][] moves;
    private int clientCount;
    public boolean started = false;

    private boolean running;

    public Client(String name, String address, int port) {
        client = new ClientBackend(name, address, port);
        boolean connect = client.openConnection(address);
        if (!connect) {
            System.err.println("Connection failed!");
        }
        String connection = "/c/" + name + "/e/";
        client.send(connection.getBytes());
        System.out.println("Attempting a connection to " + address + ":" + port + ", user: " + client.getName());
        running = true;
        run = new Thread(this, "Running");
        run.start();
    }


    public void run() {
        listen();
    }

    public void send(final byte[] message) {
        client.send(message);
    }

    public void listen() {
        listen = new Thread("Listen") {
            public void run() {
                while (running) {
                    String message = client.receive();
                    if (message.startsWith("/c/")) {
                        client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
                        client.setName(client.getName() + client.getID());
                        RoboRallyDemo.setID(client.getID());
                        System.out.println("Successfully connected to server! user: " + client.getName() + " ID: " + client.getID());
                    } else if (message.startsWith("/m/")) {
                        int player = Integer.parseInt(message.split("/m/|/e/")[1]);
                        System.out.println("Player" + player + " connected to the game.");
                    } else if (message.startsWith("/i/")) {
                        String text = "/i/" + client.getID() + "/e/";
                        send(text.getBytes());
                    } else if (message.startsWith("/u/")) {
                        String[] u = message.split("/u/|/n/|/e/");
                        //users.update(Arrays.copyOfRange(u, 1, u.length - 1));
                    } else if (message.startsWith("/r/")) {
                        int id = Integer.parseInt(message.split("/r/|/e/")[1]);
                        if (id != client.getID()) {
                            System.out.println("player " + id + " is ready");
                        }
                        RoboRallyDemo.setReady(id);
                    } else if (message.startsWith("/a/")) {
                        ready = true;
                    } else if (message.startsWith("/b/")) {
                        ready = false;
                    } else if (message.startsWith("/o/")) {
                        message = message.split("/o/|/e/")[1];
                        move = message.split("~")[0];
                        makeMoves(move);
                        orde = message.split("~")[1];
                        makeOrder(orde);
                    } else if(message.startsWith("/s/")) {
                        message = message.split("/s/|/e/")[1];
                        clientCount = Integer.parseInt(message);
                        mainMenu.setMainRunning(false);
                        started = true;
                    } else if (message.startsWith("/p/")) {
                        int id = Integer.parseInt(message.split("/p/|/e/")[1]);
                        System.out.println(RoboRallyDemo.getColors(id) + " choose to power down next round!");
                    } else if (message.startsWith("/d/")) {
                        message = message.split("/d/|/e/")[1];
                        int id = Integer.parseInt(message.split("-")[0]);
                        int count = Integer.parseInt(message.split("-")[1]);
                        RoboRallyDemo.setDead(id);
                        setClientCount(count);
                        RoboRallyDemo.setClientCount(count);
                    } else if (message.startsWith("/w/")) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (message.startsWith("/f/")) {
                        String text = message.split("/f/|/e/")[1];
                        System.out.println("Connection refused, " + text + ".");
                    }
                }
            }
        };
        listen.start();
    }

    public ClientBackend getBackendClient() {
        return client;
    }

    public boolean askReady() {
        client.send("/a//e/".getBytes());
        return ready;
    }

    public void makeMoves(String move) {
        moves = new String[clientCount][5];
        for(int j = 0; j < clientCount; j++) {
            if (RoboRallyDemo.getRobots()[j] != null) {
                for(int i = 0; i < 5; i++) {
                    String clientMove = move.split(" ")[j].split("#")[i];
                    moves[j][i] = clientMove;
                }
            }
            else {
                for (int i = 0; i < 5; i++) {
                    moves[j][i] = null;
                }
            }
        }
    }

    public void makeOrder(String orde) {
        order = new int[8*5];
        for(int i = 0; i < clientCount*5; i++){
            order[i] = Integer.parseInt(orde.split("#")[i]);
        }
    }

    public int getClientCount() {
        return clientCount;
    }

    public void setClientCount(int newCount) {
        clientCount = newCount;
    }

    public String[][] getMoves() {
        return moves;
    }

    public int[] getOrder() {
        return order;
    }

    public boolean getStarted() {
        return started;
    }
}

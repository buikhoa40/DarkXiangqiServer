package cn.yescallop.darkxiangqiserver;

import cn.yescallop.darkxiangqiserver.packet.Packet;
import cn.yescallop.darkxiangqiserver.socket.TCPServerSocket;

public class Server {

    private static Server instance;
    private TCPServerSocket socket;

    public Server() {
        this(14144);
    }

    public Server(int port) {
        this(port, "0.0.0.0");
    }

    public Server(int port, String interfaz) {
        instance = this;
        this.socket = new TCPServerSocket(port, interfaz);
        System.out.println("Binded to " + interfaz + ":" + port);
    }

    public static Server getInstance() {
        return instance;
    }

    public void close() {
        this.socket.close();
    }

    public void handlePacket(Client client, Packet packet) {
        switch (packet.pid()) {
            case Packet.MOVE_PIECE:
            case Packet.TURN_PIECE:
            case Packet.SELECT_PIECE:
                Client enemy = client.getEnemy();
                if (enemy != null) {
                    client.getEnemy().sendPacket(packet);
                }
                break;
        }
    }
}

package cn.yescallop.darkxiangqiserver;

import cn.yescallop.darkxiangqiserver.packet.StartGamePacket;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {
    private static ClientManager instance = new ClientManager();
    private final Map<String, Client> clients = new ConcurrentHashMap<>();
    private static Random random = new Random();

    private ClientManager() {
    }

    public static ClientManager getInstance() {
        return instance;
    }

    public void addClient(Channel channel) {
        Client client = new Client(channel);
        System.out.println(channel.remoteAddress().toString() + " connected");
        for (Client enemy : this.clients.values()) {
            if (!enemy.isGaming()) {
                client.startGame(enemy);
                StartGamePacket packet = new StartGamePacket();
                packet.first = random.nextBoolean();
                packet.data = generateRandomPieceData();
                client.sendPacket(packet);
                packet.first = !packet.first;
                enemy.sendPacket(packet);
                System.out.println(channel.remoteAddress().toString() + " connected to " + enemy.address().toString());
                break;
            }
        }
        this.clients.put(channel.remoteAddress().toString(), client);
    }

    private static byte[][] generateRandomPieceData() {
        byte[][] data = new byte[32][2];
        List<Integer> indexes = new ArrayList<>(32);
        for (int i = 0; i < 32; i++) {
            indexes.add(i);
        }
        data[indexes.remove(random.nextInt(indexes.size() - 1))] = new byte[]{0, 0};
        data[indexes.remove(random.nextInt(indexes.size() - 1))] = new byte[]{0, 1};
        for (byte i = 0; i < 2; i++) {
            for (byte id = 1; id < 6; id++) {
                data[indexes.remove(random.nextInt(indexes.size() - 1))] = new byte[]{id, 0};
                data[indexes.remove(random.nextInt(indexes.size() - 1))] = new byte[]{id, 1};
            }
        }
        for (byte i = 0; i < 4; i++) {
            data[indexes.remove(random.nextInt(indexes.size() - 1))] = new byte[]{6, 0};
            data[indexes.remove(random.nextInt(indexes.size() - 1))] = new byte[]{6, 1};
        }
        data[indexes.remove(random.nextInt(1))] = new byte[]{6, 0};
        data[indexes.remove(0)] = new byte[]{6, 1};
        return data;
    }

    public void removeClient(Channel channel) {
        Client enemy = this.clients.remove(channel.remoteAddress().toString()).getEnemy();
        if (enemy != null) {
            enemy.stopGame();
            try {
                enemy.channel().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(channel.remoteAddress().toString() + " disconnected from " + enemy.address().toString());
        }
        System.out.println(channel.remoteAddress().toString() + " disconnected");
    }

    public Client getClient(Channel channel) {
        return this.clients.get(channel.remoteAddress().toString());
    }
}

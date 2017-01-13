package cn.yescallop.darkxiangqiserver;

import cn.yescallop.darkxiangqiserver.packet.Packet;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.net.SocketAddress;

public class Client {

    private final Channel channel;

    private Client enemy = null;

    public Client(Channel channel) {
        this.channel = channel;
    }

    public SocketAddress address() {
        return channel.remoteAddress();
    }

    public Channel channel() {
        return channel;
    }

    public void sendPacket(Packet packet) {
        packet.encode();
        this.channel.writeAndFlush(Unpooled.wrappedBuffer(packet.getBuffer()));
        System.out.println(packet.getClass().getSimpleName() + " " + this.address().toString() + " ->");
    }

    public boolean isGaming() {
        return enemy != null;
    }

    public void startGame(Client enemy) {
        this.enemy = enemy;
        enemy.enemy = this;
    }

    public void stopGame() {
        this.enemy.enemy = null;
        this.enemy = null;
    }

    public Client getEnemy() {
        return enemy;
    }
}

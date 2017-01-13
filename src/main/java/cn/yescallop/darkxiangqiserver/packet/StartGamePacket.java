package cn.yescallop.darkxiangqiserver.packet;

import cn.yescallop.darkxiangqiserver.util.Binary;

public class StartGamePacket extends Packet {

    public boolean first;
    public byte[][] data;

    @Override
    public byte pid() {
        return START_GAME;
    }

    @Override
    public void decode() {
        this.first = this.getBoolean();
        this.data = Binary.splitBytes(this.get(), 2);
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(first);
        this.put(Binary.appendBytes(data));
    }
}

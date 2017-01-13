package cn.yescallop.darkxiangqiserver.packet;

public class TurnPiecePacket extends Packet {

    public int x;
    public int y;

    @Override
    public byte pid() {
        return TURN_PIECE;
    }

    @Override
    public void decode() {
        this.x = this.getByte();
        this.y = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.x);
        this.putByte((byte) this.y);
    }
}

package cn.yescallop.darkxiangqiserver.packet;

public class MovePiecePacket extends Packet {

    public int fromX;
    public int fromY;
    public int toX;
    public int toY;

    @Override
    public byte pid() {
        return MOVE_PIECE;
    }

    @Override
    public void decode() {
        this.fromX = this.getByte();
        this.fromY = this.getByte();
        this.toX = this.getByte();
        this.toY = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.fromX);
        this.putByte((byte) this.fromY);
        this.putByte((byte) this.toX);
        this.putByte((byte) this.toY);
    }
}

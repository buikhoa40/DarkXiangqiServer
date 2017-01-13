package cn.yescallop.darkxiangqiserver.packet;

import cn.yescallop.darkxiangqiserver.util.BinaryStream;

public abstract class Packet extends BinaryStream {

    public static final byte START_GAME = 0x01;
    public static final byte MOVE_PIECE = 0x02;
    public static final byte TURN_PIECE = 0x03;
    public static final byte SELECT_PIECE = 0x04;

    public static Packet fromBuffer(byte[] buf) {
        Packet packet;
        switch (buf[0]) {
            case START_GAME:
                packet = new StartGamePacket();
                break;
            case MOVE_PIECE:
                packet = new MovePiecePacket();
                break;
            case TURN_PIECE:
                packet = new TurnPiecePacket();
                break;
            case SELECT_PIECE:
                packet = new SelectPiecePacket();
                break;
            default:
                return null;
        }
        packet.setBuffer(buf, 1);
        packet.decode();
        return packet;
    }

    public abstract byte pid();

    public abstract void decode();

    public abstract void encode();

    @Override
    public void reset() {
        super.reset();
        this.putByte(this.pid());
    }

    public Packet clean() {
        this.setBuffer(null);
        this.setOffset(0);
        return this;
    }
}

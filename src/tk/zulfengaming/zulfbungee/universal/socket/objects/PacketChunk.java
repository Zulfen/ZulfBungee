package tk.zulfengaming.zulfbungee.universal.socket.objects;

public class PacketChunk extends Packet {

    private final boolean finalChunk;

    public PacketChunk(PacketTypes typeIn, ZulfByteBuffer byteBufferIn, boolean finalChunkIn) {
        super(typeIn, false, true, byteBufferIn);
        this.finalChunk = finalChunkIn;
        if (byteBufferIn.getData().length > 20480) {
            throw new UnsupportedOperationException("Packet chunks must not be bigger than 20000 bytes.");
        }
    }

    public boolean isFinalChunk() {
        return finalChunk;
    }

    public ZulfByteBuffer getDataSingle() {
        return (ZulfByteBuffer) data[0];
    }


}

package tk.zulfengaming.bungeesk.universal.socket;

public abstract class PacketHandler {

    private PacketTypes[] types = new PacketTypes[] {PacketTypes.NONE};

    public abstract Object handlePacket(Packet packetIn);

    public PacketTypes[] getTypes() {
        return types;
    }

    public PacketHandler(PacketTypes... types){
        this.types = types;

    }

}

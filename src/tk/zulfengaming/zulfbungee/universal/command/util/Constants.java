package tk.zulfengaming.zulfbungee.universal.command.util;

public enum Constants {

    MESSAGE_PREFIX();

    private final String toString;

    Constants() {
        this.toString = "&f&l[&b&lZulfBungee&f&l]&r ";
    }

    @Override
    public String toString() {
        return toString;
    }

}

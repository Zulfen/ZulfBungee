package tk.zulfengaming.zulfbungee.universal.command;

public enum Constants {

    MESSAGE_PREFIX("&f&l[&b&lZulfBungee&f&l]&r ");

    private final String toString;

    Constants(String s) {
        this.toString = s;
    }

    @Override
    public String toString() {
        return toString;
    }

}

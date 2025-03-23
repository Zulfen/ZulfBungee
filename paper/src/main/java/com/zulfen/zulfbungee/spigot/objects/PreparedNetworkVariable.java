package com.zulfen.zulfbungee.spigot.objects;

public class PreparedNetworkVariable {

    private final String name;
    private final Object[] data;
    private final Class<?>[] classes;

    public PreparedNetworkVariable(String name, Object[] data) {

        this.name = name;
        this.data = data;

        this.classes = new Class[data.length];
        for (int i = 0; i < data.length; i++) {
            classes[i] = data[i].getClass();
        }

    }

    public String getName() {
        return name;
    }

    public Object[] getData() {
        return data;
    }

    public Class<?>[] getClasses() {
        return classes;
    }

}

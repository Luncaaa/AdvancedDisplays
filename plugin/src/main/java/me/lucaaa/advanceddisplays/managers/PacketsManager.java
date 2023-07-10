package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.common.PacketInterface;

public class PacketsManager {
    private final PacketInterface packets;

    public PacketsManager(String version) {
        try {
            Class<?> nmsClass = Class.forName("me.lucaaa.advanceddisplays." + version + ".Packets");
            Object nmsClassInstance = nmsClass.getConstructor().newInstance();
            this.packets = (PacketInterface) nmsClassInstance;

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public PacketInterface getPackets() {
        return this.packets;
    }
}
package me.lucaaa.advanceddisplays.common.managers;

import me.lucaaa.advanceddisplays.common.PacketInterface;

public class PacketsManager {
    private static PacketInterface packets = null;

    public static void setPackets(String version) {
        try {
            Class<?> nmsClass = Class.forName("me.lucaaa.advanceddisplays." + version + ".Packets");
            Object nmsClassInstance = nmsClass.getConstructor().newInstance();
            packets = (PacketInterface) nmsClassInstance;

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static PacketInterface getPackets() {
        return packets;
    }
}
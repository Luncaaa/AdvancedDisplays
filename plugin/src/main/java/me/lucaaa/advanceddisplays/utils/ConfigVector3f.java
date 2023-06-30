package me.lucaaa.advanceddisplays.utils;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ConfigVector3f implements ConfigurationSerializable {
    private final float x;
    private final float y;
    private final float z;

    public ConfigVector3f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }

    public ConfigVector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ConfigVector3f(Vector3f vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }

    public ConfigVector3f(Map<String, Object> map) {
        this.x = (map.get("x") instanceof Float) ? (float) map.get("x") : ((Double) map.get("x")).floatValue();
        this.y = (map.get("y") instanceof Float) ? (float) map.get("y") : ((Double) map.get("y")).floatValue();
        this.z = (map.get("z") instanceof Float) ? (float) map.get("z") : ((Double) map.get("z")).floatValue();
    }

    @Nonnull
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("x", this.x);
        map.put("y", this.y);
        map.put("z", this.z);
        return map;
    }

    public Vector3f toVector3f() {
        return new Vector3f(this.x, this.y, this.z);
    }
}

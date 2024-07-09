package me.lucaaa.advanceddisplays.data;

import org.joml.Vector3f;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class ConfigVector3f {
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

    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("x", BigDecimal.valueOf(this.x).setScale(2, RoundingMode.HALF_UP).doubleValue());
        map.put("y", BigDecimal.valueOf(this.y).setScale(2, RoundingMode.HALF_UP).doubleValue());
        map.put("z", BigDecimal.valueOf(this.z).setScale(2, RoundingMode.HALF_UP).doubleValue());
        return map;
    }

    public Vector3f toVector3f() {
        return new Vector3f(this.x, this.y, this.z);
    }
}

package me.lucaaa.advanceddisplays.utils;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

public class ConfigAxisAngle4f implements ConfigurationSerializable {
    private final float a;
    private final float x;
    private final float y;
    private final float z;

    public ConfigAxisAngle4f() {
        this.a = 0.0f;
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }

    public ConfigAxisAngle4f(Quaternionf angle) {
        this.a = angle.angle();
        this.x = angle.x;
        this.y = angle.y;
        this.z = angle.z;
    }

    public ConfigAxisAngle4f(Map<String, Object> map) {
        this.a = (map.get("angle") instanceof Float) ? (float) map.get("angle") : ((Double) map.get("angle")).floatValue();
        this.x = (map.get("x") instanceof Float) ? (float) map.get("x") : ((Double) map.get("x")).floatValue();
        this.y = (map.get("y") instanceof Float) ? (float) map.get("y") : ((Double) map.get("y")).floatValue();
        this.z = (map.get("z") instanceof Float) ? (float) map.get("z") : ((Double) map.get("z")).floatValue();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("angle", this.a);
        map.put("x", this.x);
        map.put("y", this.y);
        map.put("z", this.z);
        return map;
    }

    public AxisAngle4f toAxisAngle4f() {
            return new AxisAngle4f(this.a, this.x, this.y, this.z);
    }
}

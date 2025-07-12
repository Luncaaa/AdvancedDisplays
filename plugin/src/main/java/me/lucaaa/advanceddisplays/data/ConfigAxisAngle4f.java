package me.lucaaa.advanceddisplays.data;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

public class ConfigAxisAngle4f {
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
        this.a = (float) Math.toDegrees(angle.angle());

        float sinHalfAngle = (float) Math.sqrt(1.0 - angle.w * angle.w);
        if (sinHalfAngle < 1e-6f) {
            this.x = angle.x;
            this.y = angle.y;
            this.z = angle.z;
        } else {
            this.x = angle.x / sinHalfAngle;
            this.y = angle.y / sinHalfAngle;
            this.z = angle.z / sinHalfAngle;
        }
    }

    public ConfigAxisAngle4f(Map<String, Object> map) {
        this.a = (map.get("angle") instanceof Float) ? (float) map.get("angle") : ((Double) map.get("angle")).floatValue();
        this.x = (map.get("x") instanceof Float) ? (float) map.get("x") : ((Double) map.get("x")).floatValue();
        this.y = (map.get("y") instanceof Float) ? (float) map.get("y") : ((Double) map.get("y")).floatValue();
        this.z = (map.get("z") instanceof Float) ? (float) map.get("z") : ((Double) map.get("z")).floatValue();
    }

    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("angle", Utils.round(a));
        map.put("x", Utils.round(x));
        map.put("y", Utils.round(y));
        map.put("z", Utils.round(z));
        return map;
    }

    public AxisAngle4f toAxisAngle4f() {
        return new AxisAngle4f((float) Math.toRadians(a), x, y, z);
    }
}

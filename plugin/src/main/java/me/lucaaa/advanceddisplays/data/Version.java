package me.lucaaa.advanceddisplays.data;

import java.util.List;

public enum Version {
    UNKNOWN(List.of("unknown")),
    v1_19_R3(List.of("1.19.4")),
    v1_20_R1(List.of("1.20", "1.20.1")),
    v1_20_R2(List.of("1.20.2")),
    v1_20_R3(List.of("1.20.3", "1.20.4")),
    v1_20_R4(List.of("1.20.5", "1.20.6")),
    v1_21_R1(List.of("1.21", "1.21.1", "1.21.2"));

    private final List<String> mcVersions;
    Version(List<String> mcVersions) {
        this.mcVersions = mcVersions;
    }
    public static Version getNMSVersion(String mcVersion) {
        for (Version version : values()) {
            if (version.mcVersions.contains(mcVersion)) return version;
        }
        return Version.UNKNOWN;
    }
}
package me.lucaaa.advanceddisplays.nms_common;

import java.util.List;

public enum Version {
    UNKNOWN(0, List.of("unknown")),
    v1_19_R3(1, List.of("1.19.4")),
    v1_20_R1(2, List.of("1.20", "1.20.1")),
    v1_20_R2(3, List.of("1.20.2")),
    v1_20_R3(4, List.of("1.20.3", "1.20.4")),
    v1_20_R4(5, List.of("1.20.5", "1.20.6")),
    v1_21_R1(6, List.of("1.21", "1.21.1")),
    v1_21_R2(7, List.of("1.21.2", "1.21.3")),
    v1_21_R3(8, List.of("1.21.4")),
    v1_21_R4(9, List.of("1.21.5"));

    private final List<String> mcVersions;
    private final int age; // From older to newer

    Version(int age, List<String> mcVersions) {
        this.mcVersions = mcVersions;
        this.age = age;
    }

    public static Version getNMSVersion(String mcVersion) {
        for (Version version : values()) {
            if (version.mcVersions.contains(mcVersion)) return version;
        }
        return Version.UNKNOWN;
    }

    public boolean isEqualOrNewerThan(Version version) {
        return this.age >= version.age;
    }
}
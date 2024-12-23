plugins {
    id("com.gradleup.shadow") version("9.0.0-beta4")
}

repositories {
    maven("https://repo.oraxen.com/releases")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.th0rgal:oraxen:1.186.0")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    implementation("net.kyori:adventure-platform-bungeecord:4.3.4")

    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":nms"))
    implementation(project(":nms:nms_common"))
    implementation(project(":nms:v1_19_R3", "reobf"))
    implementation(project(":nms:v1_20_R1", "reobf"))
    implementation(project(":nms:v1_20_R2", "reobf"))
    implementation(project(":nms:v1_20_R3", "reobf"))
    implementation(project(":nms:v1_20_R4", "reobf"))
    implementation(project(":nms:v1_21_R1", "reobf"))
    implementation(project(":nms:v1_21_R2", "reobf"))
    implementation(project(":nms:v1_21_R3", "reobf"))
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        exclude("org/apache/commons/io/**")
        minimize {
            exclude(project(":nms:v1_19_R3"))
            exclude(project(":nms:v1_20_R1"))
            exclude(project(":nms:v1_20_R2"))
            exclude(project(":nms:v1_20_R3"))
            exclude(project(":nms:v1_20_R4"))
            exclude(project(":nms:v1_21_R1"))
            exclude(project(":nms:v1_21_R2"))
            exclude(project(":nms:v1_21_R3"))
        }
        relocate("net.kyori", "net.kyori")
        archiveFileName.set("${project.parent?.name}-${project.version}.jar")
        destinationDirectory.set(file("../build/libs"))
    }

    assemble {
        dependsOn(shadowJar)
    }
}
plugins {
    id("io.github.goooler.shadow") version("8.1.8")
}

repositories {
    maven("https://repo.oraxen.com/releases")
}

dependencies {
    compileOnly("io.th0rgal:oraxen:1.173.0")
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    implementation("net.kyori:adventure-platform-bungeecord:4.3.3")

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
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        minimize {
            exclude(project(":nms:v1_19_R3"))
            exclude(project(":nms:v1_20_R1"))
            exclude(project(":nms:v1_20_R2"))
            exclude(project(":nms:v1_20_R3"))
            exclude(project(":nms:v1_20_R4"))
            exclude(project(":nms:v1_21_R1"))
        }
        relocate("net.kyori", "net.kyori")
        archiveFileName.set("${project.parent?.name}-${project.version}.jar")
        destinationDirectory.set(file("../build/libs"))
    }

    assemble {
        dependsOn(shadowJar)
    }
}
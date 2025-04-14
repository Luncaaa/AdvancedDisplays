plugins {
    id("java")
}

allprojects {
    apply(plugin = "java")
    group = "me.lucaaa"
    version = "1.6"

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    tasks {
        compileJava {
            options.release = 17
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
        compileOnly("io.netty:netty-all:4.2.0.Final")
        implementation("net.kyori:adventure-api:4.20.0")
        implementation("net.kyori:adventure-text-minimessage:4.20.0")
        implementation("net.kyori:adventure-text-serializer-legacy:4.20.0")
        implementation("net.kyori:adventure-text-serializer-gson:4.20.0")
        implementation("net.kyori:adventure-text-serializer-bungeecord:4.3.4")
    }
}
plugins {
    id("java")
}

allprojects {
    apply(plugin = "java")
    group = "me.lucaaa"
    version = "1.5.7"

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
        compileOnly("io.netty:netty-all:4.1.113.Final")
    }
}
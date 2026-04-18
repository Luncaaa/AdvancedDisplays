plugins {
    id("java")
}

allprojects {
    apply(plugin = "java")
    group = "me.lucaaa"
    version = "1.7.1"

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
            options.release = 17
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
        compileOnly("io.netty:netty-all:4.2.12.Final")
        implementation("net.kyori:adventure-api:4.26.1")
        implementation("net.kyori:adventure-text-minimessage:4.26.1")
        implementation("net.kyori:adventure-text-serializer-legacy:4.26.1")
        implementation("net.kyori:adventure-text-serializer-gson:4.26.1")
        implementation("net.kyori:adventure-platform-bukkit:4.4.1")
    }
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.BIN
    }

    jar {
        enabled = false
    }
}
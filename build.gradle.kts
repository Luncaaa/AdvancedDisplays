plugins {
    id("java")
    id("shared")
    id("com.gradleup.shadow") version("latest.release") apply false
    id("io.papermc.hangar-publish-plugin") version("latest.release") apply false
    id("com.modrinth.minotaur") version("latest.release") apply false
    id("io.papermc.paperweight.userdev") version ("latest.release") apply false
}

group = "me.lucaaa"
version = "1.7.2"

extra.set("releaseInfo", getReleaseData(project.version))

allprojects {
    plugins.apply("java")
    plugins.apply("shared")

    group = rootProject.group
    version = rootProject.version

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
            options.release = 21
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
        compileOnly("io.netty:netty-all:4.2.16.Final")
        implementation("net.kyori:adventure-api:5.2.0")
        implementation("net.kyori:adventure-text-minimessage:5.2.0")
        implementation("net.kyori:adventure-text-serializer-legacy:5.2.0")
        implementation("net.kyori:adventure-text-serializer-gson:5.2.0")
    }
}

tasks {
    jar {
        enabled = false
    }
}
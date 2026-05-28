plugins {
    id("java")
    id("com.gradleup.shadow") version("latest.release")
    id("io.papermc.hangar-publish-plugin") version("latest.release")
    id("com.modrinth.minotaur") version("latest.release")
}

group = "me.lucaaa"
version = "1.7.1"

val releaseInfo by extra { getReleaseData(project.version) }

allprojects {
    apply(plugin = "java")

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
        compileOnly("io.netty:netty-all:4.2.14.Final")
        implementation("net.kyori:adventure-api:5.1.1")
        implementation("net.kyori:adventure-text-minimessage:5.1.1")
        implementation("net.kyori:adventure-text-serializer-legacy:5.1.1")
        implementation("net.kyori:adventure-text-serializer-gson:5.1.1")
    }
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.BIN
    }

    jar {
        enabled = false
    }

    shadowJar {
        enabled = false
    }
}
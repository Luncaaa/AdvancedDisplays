plugins {
    id("com.gradleup.shadow") version("latest.release")
}

repositories {
    maven("https://repo.oraxen.com/releases")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.th0rgal:oraxen:1.186.0")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")
    implementation("net.kyori:adventure-api:4.18.0")
    implementation("net.kyori:adventure-text-minimessage:4.18.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.18.0")
    implementation("net.kyori:adventure-platform-bungeecord:4.3.4")

    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":nms"))
    implementation(project(":nms:nms_common"))

    file("${rootDir}/nms").listFiles()!!.filter { it.isDirectory && it.name.startsWith("v") }.forEach {
        implementation(project(":nms:${it.name}", "reobf"))
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        exclude("org/apache/commons/io/**")
        minimize {
            file("${rootDir}/nms").listFiles()!!.filter { it.isDirectory && it.name.startsWith("v") }.forEach {
                exclude(project(":nms:${it.name}"))
            }
        }
        relocate("net.kyori", "net.kyori")
        archiveFileName.set("${project.parent?.name}-${project.version}.jar")
        destinationDirectory.set(file("../build/libs"))
    }

    assemble {
        dependsOn(shadowJar)
    }
}
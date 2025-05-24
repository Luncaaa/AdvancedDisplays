plugins {
    id("com.gradleup.shadow") version("latest.release")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.oraxen.com/releases")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("io.th0rgal:oraxen:1.190.0")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")


    implementation(project(":api"))
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
        exclude("org/apache/commons/io/**", "com/google/gson/**")
        minimize {
            file("${rootDir}/nms").listFiles()!!.filter { it.isDirectory && it.name.startsWith("v") }.forEach {
                exclude(project(":nms:${it.name}"))
            }
        }
        relocate("net.kyori", "shaded.net.kyori")
        archiveFileName.set("${project.parent?.name}-${project.version}.jar")
        destinationDirectory.set(file("../build/libs"))
    }

    assemble {
        dependsOn(shadowJar)
    }
}
plugins {
    id("com.gradleup.shadow")
    id("io.papermc.hangar-publish-plugin")
    id("com.modrinth.minotaur")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.oraxen.com/releases")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("io.th0rgal:oraxen:1.190.0")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")

    implementation(project(":api"))

    implementation(project(":platform"))
    implementation(project(":platform:spigot"))
    implementation(project(":platform:folia"))
    implementation(project(":platform:common"))

    implementation(project(":nms"))
    implementation(project(":nms:nms_common"))
    file("${rootDir}/nms").listFiles()!!.filter { it.isDirectory && it.name.startsWith("v") }.forEach {
        if (it.name.startsWith("v1")) {
            implementation(project(":nms:${it.name}", "reobf"))
        } else {
            implementation(project(":nms:${it.name}", "mojangMapped"))
        }
    }
}

tasks {
    shadowJar {
        exclude("org/apache/commons/io/**", "com/google/gson/**")
        minimize {
            file("${rootDir}/nms").listFiles()!!.filter { it.isDirectory && it.name.startsWith("v") }.forEach {
                exclude(project(":nms:${it.name}"))
            }
        }
        relocate("net.kyori", "shaded.net.kyori")
        archiveFileName.set("${project.parent?.name}-${project.version}.jar")
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }

    assemble {
        dependsOn(shadowJar)
    }

    register("publishToSites") {
        dependsOn(publishAllPublicationsToHangar)
        dependsOn(modrinth)
    }
}

val data = rootProject.extra["releaseInfo"] as ReleaseData

hangarPublish {
    publications.register("plugin") {
        version = project.version as String
        id = "AdvancedDisplays"
        channel = "Release"
        changelog = data.body

        apiKey = System.getenv("HANGAR_KEY")

        platforms {
            paper {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions = listOf("1.19.4", "1.20.x", "1.21.x", "26.1.x")
                dependencies {
                    hangar("PlaceholderAPI") {
                        required = false
                    }
                }
            }
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(data.modrinthId)
    versionNumber.set(project.version as String)
    uploadFile.set(tasks.shadowJar)
    gameVersions.addAll(data.versions)
    loaders.addAll("spigot", "paper", "purpur", "folia")

    versionName = data.name
    changelog = data.body

    dependencies {
        optional.project("placeholderapi")
    }
}
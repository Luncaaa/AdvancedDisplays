plugins {
    id("maven-publish")
}

java {
    withJavadocJar()
}

dependencies {
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    implementation("net.kyori:adventure-platform-bungeecord:4.3.3")
}

tasks {
    javadoc {
        title = "AdvancedDisplays API " + project.version
        options {
            (this as StandardJavadocDocletOptions).apply {
                // ©
                bottom = "Copyright 2024 Lucaaa. All rights reserved. Licensed under GPL 3.0. View the source code <a href=\"https://github.com/Luncaaa/AdvancedDisplays\">here</a>"
                links = listOf("https://hub.spigotmc.org/javadocs/spigot", "https://jd.advntr.dev/api/4.17.0/")
                header = "<div style=\"font-size: 25px\"><a href=\"https://github.com/Luncaaa\">By Lucaaa</a>    |    <a href=\"https://spigotmc.org/resources/authors/lucaaa.1192446/\">More plugins</a></div>"
            }
        }
    }

    assemble {
        finalizedBy(publishToMavenLocal)
    }
}

publishing {
    publications {
        val mavenJava by creating(MavenPublication::class) {
            groupId = "AdvancedDisplays"
            artifactId = "advanceddisplays-api"
            version = "1.5.3"

            from(components["java"])
        }
    }
}

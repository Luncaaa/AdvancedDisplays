plugins {
    id("maven-publish")
}

java {
    withJavadocJar()
}

tasks {
    javadoc {
        title = "AdvancedDisplays API " + project.version
        options {
            (this as StandardJavadocDocletOptions).apply {
                charSet = "UTF-8"
                encoding = "UTF-8"
                docEncoding = "UTF-8"
                bottom = "Copyright © 2026 Lucaaa. All rights reserved. Licensed under GPL 3.0. View the source code <a href=\"https://github.com/Luncaaa/AdvancedDisplays\">here</a>"
                links = listOf("https://hub.spigotmc.org/javadocs/spigot", "https://jd.papermc.io/adventure/5.2.0/")
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
        create<MavenPublication>("mavenJava") {
            groupId = "AdvancedDisplays"
            artifactId = "advanceddisplays-api"
            version = "${project.version}"

            from(components["java"])
        }
    }
}
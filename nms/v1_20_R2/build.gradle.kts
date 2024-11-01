plugins {
    id("io.papermc.paperweight.userdev") version "1.7.4"
}

dependencies {
    implementation(project(":nms:nms_common"))
    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}
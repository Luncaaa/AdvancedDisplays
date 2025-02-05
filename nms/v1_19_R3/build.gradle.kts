plugins {
    id("io.papermc.paperweight.userdev") version "latest.release"
}

dependencies {
    implementation(project(":nms:nms_common"))
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}
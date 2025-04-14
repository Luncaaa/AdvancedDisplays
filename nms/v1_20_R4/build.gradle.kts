plugins {
    id("io.papermc.paperweight.userdev") version "latest.release"
}

dependencies {
    implementation(project(":nms:nms_common"))
    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

tasks {
    compileJava {
        options.release = 21
    }
}
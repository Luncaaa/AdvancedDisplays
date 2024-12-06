plugins {
    id("io.papermc.paperweight.userdev") version "1.7.7"
}

dependencies {
    implementation(project(":nms:nms_common"))
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION
        options.release = 21
    }
}
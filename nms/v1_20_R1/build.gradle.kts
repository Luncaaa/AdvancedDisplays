plugins {
    id("io.papermc.paperweight.userdev") version "latest.release"
}

dependencies {
    implementation(project(":nms:nms_common"))
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

paperweight {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}
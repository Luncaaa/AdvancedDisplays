plugins {
    id("io.papermc.paperweight.userdev") version "latest.release"
}

configurations.all {
    resolutionStrategy.capabilitiesResolution.withCapability("org.spigotmc:spigot-api") {
        val toBeSelected = candidates.firstOrNull { it.id.let { id -> id is ModuleComponentIdentifier && id.module == "spigot-api" } }
        if (toBeSelected != null) {
            select(toBeSelected)
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":nms:nms_common"))
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
}

paperweight {
    reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    compileJava {
        options.release = 21
    }
}
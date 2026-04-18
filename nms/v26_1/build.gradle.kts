plugins {
    id("io.papermc.paperweight.userdev") version "latest.release"
}

configurations.all {
    resolutionStrategy.capabilitiesResolution.withCapability("org.spigotmc:spigot-api") {
        val toBeSelected = candidates.firstOrNull { it.id.let { id -> id is ModuleComponentIdentifier && id.module == "paper-api" } }
        if (toBeSelected != null) {
            select(toBeSelected)
        }
    }
}

dependencies {
    implementation(project(":nms:nms_common"))
    paperweight.paperDevBundle("26.1.2.build.+")
}

paperweight {
    reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

tasks {
    compileJava {
        options.release = 25
    }

    reobfJar {
        enabled = false
    }
}

val mojangMapped by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add("mojangMapped", tasks.jar)
}
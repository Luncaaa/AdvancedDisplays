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

dependencies {
    implementation(project(":nms:nms_common"))
    paperweight.paperDevBundle("1.21.7-R0.1-SNAPSHOT") // Important for the custom model data component
    compileOnly("org.spigotmc:spigot-api:1.21.7-R0.1-SNAPSHOT")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

tasks {
    compileJava {
        options.release = 21
    }
}
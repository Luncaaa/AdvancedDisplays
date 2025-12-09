subprojects {
    dependencies {
        // So that minimizing does not include adventure.
        // After all, these dependencies will already be shaded in the final JAR.
        compileOnly(project(":api"))

        implementation(project(":platform"))
        implementation(project(":platform:spigot"))
        implementation(project(":platform:folia"))
        implementation(project(":platform:common"))
    }
}
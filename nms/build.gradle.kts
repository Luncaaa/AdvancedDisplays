subprojects {
    dependencies {
        // So that minimizing does not include adventure.
        // After all, these dependencies will already be shaded in the final JAR.
        compileOnly(project(":common"))
        compileOnly(project(":api"))
    }
}
rootProject.name = "AdvancedDisplays"
include("plugin", "api", "common", "nms")
file("nms").listFiles()
    ?.filter { it.isDirectory }
    ?.forEach { subDir ->
        val moduleName = subDir.name
        include("nms:$moduleName")
        findProject(":nms:$moduleName")?.name = moduleName
    }
rootProject.name = "AdvancedDisplays"
include("plugin", "api", "nms")
include("platform", "platform:common", "platform:folia", "platform:spigot")
file("nms").listFiles()
    ?.filter { it.isDirectory }
    ?.forEach { subDir ->
        val moduleName = subDir.name
        include("nms:$moduleName")
        findProject(":nms:$moduleName")?.name = moduleName
    }
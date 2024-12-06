rootProject.name = "AdvancedDisplays"
include("plugin", "api", "common", "nms")
include("nms:nms_common", "nms:v1_19_R3", "nms:v1_20_R1", "nms:v1_20_R2", "nms:v1_20_R3", "nms:v1_20_R4", "nms:v1_21_R1", "nms:v1_21_R2", "nms:v1_21_R3")
findProject(":nms:nms_common")?.name = "nms_common"
findProject(":nms:v1_19_R3")?.name = "v1_19_R3"
findProject(":nms:v1_20_R1")?.name = "v1_20_R1"
findProject(":nms:v1_20_R2")?.name = "v1_20_R2"
findProject(":nms:v1_20_R3")?.name = "v1_20_R3"
findProject(":nms:v1_20_R4")?.name = "v1_20_R4"
findProject(":nms:v1_21_R1")?.name = "v1_21_R1"
findProject(":nms:v1_21_R2")?.name = "v1_21_R2"
findProject(":nms:v1_21_R3")?.name = "v1_21_R3"
import groovy.json.JsonSlurper
import java.net.URI

data class ReleaseData(val name: String, val body: String, val versions: List<String>, val modrinthId: String = "tDoPNQrP")

fun getReleaseData(version: Any): ReleaseData {
    val supportedVersions = listOf(
        "1.19.4", "1.20.1", "1.20.2", "1.20.3", "1.20.4",
        "1.20.5", "1.20.6", "1.21", "1.21.1", "1.21.2",
        "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7",
        "1.21.8", "1.21.9", "1.21.10", "1.21.11", "26.1",
        "26.1.1", "26.1.2"
    )

    try {
        val url = "https://api.github.com/repos/Luncaaa/AdvancedDisplays/releases/tags/$version"
        val api = URI(url).toURL().readText()
        val json = JsonSlurper().parseText(api) as Map<*, *>

        val name = json["name"] as String
        val rawBody = json["body"] as String

        val formattedBody = rawBody.replace("\r\n", "\n")
            .replace("\r", "\n")
            .replace("\n", "  \n")

        return ReleaseData(name, "# $name\n$formattedBody", supportedVersions)
    } catch (_: Exception) {
        return ReleaseData("Release $version", "Changelog not found for tag: $version", supportedVersions)
    }
}
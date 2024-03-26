# AdvancedDisplays
Create block, item and text displays!

## Links & Support
You can download the plugin here:
- Spigot resource page: https://www.spigotmc.org/resources/advanceddisplays.110865/
- Source code on GitHub: https://github.com/Luncaaa/AdvancedDisplays
- Wiki: https://lucaaa.gitbook.io/advanceddisplays/

If you have an issue, found a bug or want to suggest something, you can do it here:
- Spigot discussion page: https://www.spigotmc.org/threads/advanceddisplays.609868/
- GitHub issues: https://github.com/Luncaaa/AdvancedDisplays/issues
- Contact me on Discord: Lucaaa#6268 / luncaaa

## How to compile
The plugin is compiled using Maven and Java 17.
Build the jar running the Maven command "mvn clean install package -P build-jar" in the parent POM.
> **&#9432;** You will have to install the NMS classes before compiling!

## Developer API [![](https://jitpack.io/v/Luncaaa/AdvancedDisplays.svg)](https://jitpack.io/#Luncaaa/AdvancedDisplays)
> You can find the docs [here](https://jitpack.io/com/github/Luncaaa/AdvancedDisplays/advanceddisplays-api/main-SNAPSHOT/javadoc/)
<details>
<summary>Maven</summary>

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>com.github.Luncaaa.AdvancedDisplays</groupId>
        <artifactId>advanceddisplays-api</artifactId>
        <version>{PLUGIN VERSION}</version>
    </dependency>
</dependencies>
```
</details>

<details>
<summary>Gradle</summary>

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.Luncaaa.AdvancedDisplays:advanceddisplays-api:{PLUGIN VERSION}'
}
```
</details>
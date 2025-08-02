# AdvancedDisplays
Create entity, block, item and text displays!

## Links & Support
You can download the plugin here:
- Spigot resource page: https://www.spigotmc.org/resources/advanceddisplays.110865/
- Hangar resource page: https://hangar.papermc.io/Lucaaa/AdvancedDisplays
- Modrinth resource page: https://modrinth.com/plugin/advanceddisplays
- Wiki: https://lucaaa.gitbook.io/advanceddisplays/
- Javadocs: https://javadoc.jitpack.io/com/github/Luncaaa/AdvancedDisplays/main-SNAPSHOT/javadoc
- Dev build: https://github.com/Luncaaa/AdvancedDisplays/releases/tag/dev

If you have an issue, found a bug or want to suggest something, you can do it here:
- Spigot discussion page: https://www.spigotmc.org/threads/advanceddisplays.609868/
- GitHub issues: https://github.com/Luncaaa/AdvancedDisplays/issues
- Contact me on Discord: Lucaaa#6268 / luncaaa

## How to compile
The plugin is compiled using Gradle and Java 21. It can run on Java 17 on versions <1.20.5, but it must run with Java 21 on >=1.20.5.
Build the jar running the Gradle command "./gradlew build" in the root folder.

## Developer API [![](https://jitpack.io/v/Luncaaa/AdvancedDisplays.svg)](https://jitpack.io/#Luncaaa/AdvancedDisplays)
> You can find the docs [here](https://javadoc.jitpack.io/com/github/Luncaaa/AdvancedDisplays/main-SNAPSHOT/javadoc)
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
        <groupId>com.github.Luncaaa</groupId>
        <artifactId>AdvancedDisplays</artifactId>
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
    compileOnly 'com.github.Luncaaa:AdvancedDisplays:{PLUGIN VERSION}'
}
```
</details>

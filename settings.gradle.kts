pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.wallentines.org/plugins")
    }
    includeBuild("gradle/plugins")
}

rootProject.name = "midnightlib"

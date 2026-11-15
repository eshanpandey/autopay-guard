pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositories { google(); mavenCentral() }
}
rootProject.name = "autopay-guard-spike"
include(":app")
include(":parser")
project(":parser").projectDir = file("../parser")

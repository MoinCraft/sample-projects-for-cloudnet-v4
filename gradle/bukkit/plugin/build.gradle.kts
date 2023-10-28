plugins {
    id("java")
}

group = "com.github.moincraft.gradle.bukkit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "spigot-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    // Spigot
    val spigotVersion = "1.20.2-R0.1-SNAPSHOT"
    implementation("org.spigotmc", "spigot-api", spigotVersion)

    // CloudNet
    val cloudNetVersion = "4.0.0-RC9"
    implementation("eu.cloudnetservice.cloudnet", "bridge", cloudNetVersion)
    implementation("eu.cloudnetservice.cloudnet", "platform-inject-api", cloudNetVersion)
    annotationProcessor("eu.cloudnetservice.cloudnet", "platform-inject-processor", cloudNetVersion)
}

tasks.test {
    useJUnitPlatform()
}

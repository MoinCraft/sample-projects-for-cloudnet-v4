plugins {
    id("java")
}

group = "com.github.moincraft.gradle.bukkit"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "spigot-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    // Spigot
    val spigotVersion = "1.20.4-R0.1-SNAPSHOT"
    implementation("org.spigotmc", "spigot-api", spigotVersion)

    // CloudNet
    val cloudNetVersion = "4.0.0-RC10"
    implementation(platform("eu.cloudnetservice.cloudnet:bom:$cloudNetVersion"))
    implementation("eu.cloudnetservice.cloudnet", "bridge")
    implementation("eu.cloudnetservice.cloudnet", "wrapper-jvm")
    implementation("eu.cloudnetservice.cloudnet", "platform-inject-api")
    annotationProcessor("eu.cloudnetservice.cloudnet", "platform-inject-processor", cloudNetVersion)
}

tasks.test {
    useJUnitPlatform()
}

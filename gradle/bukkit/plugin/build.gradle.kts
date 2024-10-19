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
    // BOMs
    implementation(platform(libs.cloudnet.bom))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    implementation(libs.bundles.spigot.plugin)
    annotationProcessor(libs.cloudnet.platform.inject.processor)
}

tasks.test {
    useJUnitPlatform()
}

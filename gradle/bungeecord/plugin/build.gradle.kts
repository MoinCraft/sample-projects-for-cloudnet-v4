plugins {
    id("java")
}

group = "com.github.moincraft.gradle.bungeecord"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "bungeecord-repo"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    maven {
        name = "mojang-repo"
        url = uri("https://libraries.minecraft.net")
    }
}

dependencies {
    // BOMs
    implementation(platform(libs.cloudnet.bom))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    implementation(libs.bundles.bungeecord.plugin)
    annotationProcessor(libs.cloudnet.platform.inject.processor)
}

tasks.test {
    useJUnitPlatform()
}

plugins {
    id("java")
    alias(libs.plugins.juppiter)
}

group = "com.github.moincraft.gradle.cloudnet"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "spigot-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven("https://repository.derklaro.dev/releases/") {
        mavenContent {
            releasesOnly()
        }
    }
    maven("https://repository.derklaro.dev/snapshots/") {
        mavenContent {
            snapshotsOnly()
        }
    }
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    // BOMs
    implementation(platform(libs.cloudnet.bom))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    implementation(libs.bundles.spigot.plugin)
    implementation(libs.bundles.node.module)
    annotationProcessor(libs.cloudnet.platform.inject.processor)
}

tasks.test {
    useJUnitPlatform()
}

moduleJson {
    name = "SampleRPC"
    author = "MoinCraft aka. GiantTree"
    main = "com.github.moincraft.cloudnet.module.platform.node.RPCSampleModule"
}

tasks.withType<Copy>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

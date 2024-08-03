plugins {
    id("java")
    id("eu.cloudnetservice.juppiter") version "0.4.0"
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
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.slf4j", "slf4j-api", "2.0.13")

    // Spigot
    val spigotVersion = "1.21-R0.1-SNAPSHOT"
    implementation("org.spigotmc", "spigot-api", spigotVersion)

    // CloudNet
    val cloudNetVersion = "4.0.0-RC10"
    implementation(platform("eu.cloudnetservice.cloudnet:bom:$cloudNetVersion"))
    implementation("eu.cloudnetservice.cloudnet", "node")
    implementation("eu.cloudnetservice.cloudnet", "platform-inject-api")
    annotationProcessor("eu.cloudnetservice.cloudnet", "platform-inject-processor", cloudNetVersion)
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

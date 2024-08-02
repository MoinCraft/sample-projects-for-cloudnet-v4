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
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // BungeeCord
    val bungeecordVersion = "1.21-R0.1-SNAPSHOT"
    implementation("net.md-5", "bungeecord-api", bungeecordVersion)

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

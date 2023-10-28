plugins {
    id("java")
}

group = "com.github.moincraft.gradle"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}

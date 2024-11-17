import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import eu.cloudnetservice.gradle.juppiter.ModuleConfiguration

plugins {
    id("java")
    alias(libs.plugins.juppiter)
    alias(libs.plugins.shadow)
}

group = "com.github.moincraft.gradle.cloudnet"
version = "1.0-SNAPSHOT"

repositories {
//    mavenLocal()
    mavenCentral()
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
    compileOnly(platform(libs.cloudnet.bom))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    compileOnly(libs.bundles.node.module)
    compileOnly(libs.prettytime.nlp)
    compileOnly(libs.natty)
    implementation(group = "org.slf4j", name = "slf4j-nop", version = "1.7.36")
    annotationProcessor(libs.cloudnet.platform.inject.processor)
}

tasks.test {
    useJUnitPlatform()
}

moduleJson {
    name = "Command-Scheduler"
    author = "MoinCraft aka. GiantTree"
    main = "com.github.moincraft.cloudnet.module.commandscheduler.CommandSchedulerModule"
    dependencies.add(buildModuleDependency(libs.prettytime.nlp))
    dependencies.add(buildModuleDependency(libs.natty))
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<ShadowJar> {
    // Don't set a classifier for the shadow jar
    this.archiveClassifier.set("")
    // Relocate the slf4j package to fix warnings due to PrettyTime-NLP
    this.relocate("org.slf4j", "org.ocpsoft.prettytime.shade.org.slf4j")
}

fun buildModuleDependency(dependency: Provider<MinimalExternalModuleDependency>): ModuleConfiguration.Dependency {
    return ModuleConfiguration.Dependency(dependency.get().module.name).apply {
        group = dependency.get().module.group
        version = dependency.get().versionConstraint.requiredVersion
    }
}

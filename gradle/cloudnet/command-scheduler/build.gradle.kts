import eu.cloudnetservice.gradle.juppiter.ModuleConfiguration

plugins {
    id("java")
    alias(libs.plugins.juppiter)
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
    implementation(platform(libs.cloudnet.bom))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    implementation(libs.bundles.node.module)
    implementation(libs.prettytime.nlp)
    implementation(libs.natty)
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

tasks.withType<Copy>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

fun buildModuleDependency(dependency: Provider<MinimalExternalModuleDependency>): ModuleConfiguration.Dependency {
    return ModuleConfiguration.Dependency(dependency.get().module.name).apply {
        group = dependency.get().module.group
        version = dependency.get().versionConstraint.requiredVersion
    }
}

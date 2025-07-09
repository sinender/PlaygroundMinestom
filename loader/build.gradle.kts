
plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application

    // shading is needed for building a jar
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

val includedProjects = listOf(
    "block-update-system",
    "blocks",
    "crafting",
    "entities",
    "entity-meta",
    "fluid-simulation",
    "item-placeables",
    "items",
    "mojang-data",
    "commands",
    "instance-meta",
    "datapack-loading",
    "core"
)

val projectsVersion = mapOf(
    "datapack-loading" to "1.1.1"
)

dependencies {
    // Project "loader" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
    implementation(project(":utils"))
    implementation(project(":common"))
    implementation(project(":lobby"))

    implementation(libs.minestom)
    implementation(libs.reflections)
    implementation("io.github.revxrsal:lamp.common:${libs.versions.lamp.get()}")
    implementation("io.github.revxrsal:lamp.minestom:${libs.versions.lamp.get()}")

    includedProjects.forEach { module ->
        implementation("net.minestom.vanilla:$module:${libs.versions.vanillaImpl.get()}")
    }

    implementation("org.everbuild.blocksandstuff:blocksandstuff-common:1.2.0-SNAPSHOT")
    implementation("org.everbuild.blocksandstuff:blocksandstuff-blocks:1.2.0-SNAPSHOT")
    implementation("org.everbuild.blocksandstuff:blocksandstuff-fluids:1.2.0-SNAPSHOT")


    implementation(libs.mccoroutineMinestomApi)
    implementation(libs.mccoroutineMinestomCore)
    implementation(libs.kotlinxCoroutines)

    implementation("com.github.ben-manes.caffeine:caffeine:3.2.1")

    implementation(libs.minestomPVP)

}

application {
    // Define the main class for the application.
    mainClass.set("net.sinender.app.PlaygroundKt")
}

tasks.shadowJar {
    mergeServiceFiles()
}
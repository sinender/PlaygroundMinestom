plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application

    // shading is needed for building a jar
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

dependencies {
    // Project "loader" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
    implementation(project(":utils"))
    implementation(project(":common"))
    implementation(project(":lobby"))

    implementation(libs.minestom)
    implementation(libs.reflections)
    implementation("io.github.revxrsal:lamp.common:${libs.versions.lamp.get()}")
    implementation("io.github.revxrsal:lamp.minestom:${libs.versions.lamp.get()}")

    implementation(libs.mccoroutineMinestomApi)
    implementation(libs.mccoroutineMinestomCore)
    implementation(libs.kotlinxCoroutines)

    implementation(libs.minestomPVP)
}

application {
    mainClass = "net.sinender.app.PlaygroundKt"
}

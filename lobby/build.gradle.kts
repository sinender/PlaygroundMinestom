plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application
}

dependencies {
    // Project "loader" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
    implementation(project(":utils"))

    compileOnly(libs.minestom)

    implementation(libs.mccoroutineMinestomApi)
    implementation(libs.mccoroutineMinestomCore)
    implementation(libs.kotlinxCoroutines)
}

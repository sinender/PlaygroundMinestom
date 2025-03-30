plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    // Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
    implementation(project(":utils"))
    implementation(libs.polar)
    implementation(libs.minestom)
    implementation("io.github.revxrsal:lamp.common:${libs.versions.lamp.get()}")
    implementation("io.github.revxrsal:lamp.minestom:${libs.versions.lamp.get()}")
}

kotlin {
    compilerOptions {
        javaParameters = true
    }
}
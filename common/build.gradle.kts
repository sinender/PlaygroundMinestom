plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    // Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
    implementation(project(":utils"))
    implementation(project(":lobby"))
    implementation(libs.polar)
    compileOnly(libs.minestom)

    implementation(libs.bundles.exposed)
    implementation(libs.postgresql)
    implementation(libs.hikariCP)
    implementation(libs.flywayCore)
    implementation(libs.flywayPostgresql)
    implementation(libs.json)

    implementation(libs.reflections)

    implementation("io.github.revxrsal:lamp.common:${libs.versions.lamp.get()}")
    implementation("io.github.revxrsal:lamp.minestom:${libs.versions.lamp.get()}")

    implementation(libs.mccoroutineMinestomApi)
    implementation(libs.mccoroutineMinestomCore)
    implementation(libs.kotlinxCoroutines)

    compileOnly(libs.minestomPVP)
}

kotlin {
    compilerOptions {
        javaParameters = true
    }
}
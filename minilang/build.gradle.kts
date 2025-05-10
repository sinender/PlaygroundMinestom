import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("com.strumenta.antlr-kotlin") version "1.0.3"
}

dependencies {
    implementation("com.strumenta:antlr-kotlin-runtime:1.0.3")
}

sourceSets {
    main {
        java.srcDirs(
            layout.projectDirectory.dir("src/main/kotlin"),
            layout.projectDirectory.dir("src/main/antlr"),
            layout.buildDirectory.dir("generatedAntlr")
        )
    }
    test {
        java.srcDirs(
            layout.projectDirectory.dir("test/kotlin")
        )
    }
}

val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    dependsOn("cleanGenerateKotlinGrammarSource")

    source = fileTree("src/main/antlr") {
        include("**/*.g4")
    }

    // We want the generated source files to have this package name
    val pkgName = "com.strumenta.antlrkotlin.parsers.generated"
    packageName = pkgName

    // We want visitors alongside listeners.
    arguments = listOf("-visitor", "-no-listener")

    // Generated files are outputted inside build/generatedAntlr/{package-name}
    val outDir = "generatedAntlr/${pkgName.replace(".", "/")}"
    outputDirectory = layout.buildDirectory.dir(outDir).get().asFile
}

tasks.withType<KotlinCompile> {
    dependsOn(generateKotlinGrammarSource)
}
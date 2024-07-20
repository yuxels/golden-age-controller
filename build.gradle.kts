import de.undercouch.gradle.tasks.download.Download

plugins {
    java
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.babric.loom.extension)
    alias(libs.plugins.download)
}

base {
    group = "cat.kittens.mods"
    archivesName = "babric-controller"

    // Semantic Versioning: https://semver.org/
    version = "0.1.0"
    version = buildString {
        val isReleaseBuild = project.hasProperty("build.release")
        val buildId = System.getenv("GITHUB_RUN_NUMBER")
        if (isReleaseBuild) {
            append(version.toString())
        } else {
            append(version.toString().substringBefore('-'))
            append("-snapshot")
        }
        append("+mc").append(libs.versions.minecraft.get())
        if (!isReleaseBuild)
            append(if (buildId != null) "-build.${buildId}" else "-local")
    }
}

loom {
    @Suppress("UnstableApiUsage")
    mixin {
        useLegacyMixinAp = true
    }
    customMinecraftMetadata.set("https://babric.github.io/manifest-polyfill/${libs.versions.minecraft.get()}.json")
    intermediaryUrl.set("https://maven.glass-launcher.net/babric/babric/intermediary/%1\$s/intermediary-%1\$s-v2.jar")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
}

repositories {
    maven(url = "https://maven.glass-launcher.net/babric/") {
        name = "Babric"
    }
    maven(url = "https://jitpack.io/") {
        name = "JitPack"
    }
    maven(url = "https://maven.glass-launcher.net/releases") {
        name = "Glass Releases"
    }
    maven(url = "https://maven.glass-launcher.net/snapshots") {
        name = "Glass Snapshots"
    }
    maven(url = "https://maven.minecraftforge.net/") {
        name = "Minecraft Forge"
    }
    maven(url = "https://maven.wispforest.io")
    exclusiveContent {
        forRepository {
            maven(url = "https://api.modrinth.com/maven") {
                name = "Modrinth"
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
    maven(url = "https://maven.isxander.dev/releases")
    maven(url = "https://maven.isxander.dev/snapshots")
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.mappings) {
        classifier("v2")
    })
    modImplementation(libs.babric.loader)
    modImplementation(libs.station.api)
    implementation(libs.sdl4j)
    implementation(libs.bundles.log)
    implementation(libs.commons.lang3)
    implementation(libs.jna)
    implementation(libs.guava)
    implementation(libs.jankson)
    modImplementation(libs.mod.menu) {
        isTransitive = false
    }
}

configurations.all {
    exclude(group = "org.ow2.asm", module = "asm-debug-all")
    exclude(group = "org.ow2.asm", module = "asm-all")
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${archiveBaseName.get()}" }
    }
}

tasks.processResources {
    inputs.property("version", version)
    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}

val sdl3Version = libs.versions.sdl4j.get().substringBefore("-")
val baseUrl = "https://maven.isxander.dev/releases/dev/isxander/libsdl4j-natives/$sdl3Version"
val destDir = project.layout.buildDirectory.dir("sdl3-natives").get().asFile
val nativesUrls = listOf(
    "windows64.dll" to "Windows64",
    "windows32.dll" to "Windows32",
    "linux64.so" to "Linux64",
    "macos-x86_64.dylib" to "Darwin64",
    "macos-aarch64.dylib" to "DarwinARM64"
)
    .map {
        ("libsdl4j-natives-${it.first}" to "downloadSDL3Natives${it.second}") to
                "$baseUrl/libsdl4j-natives-$sdl3Version-${it.first}"
    }
nativesUrls.forEach { (key, it) ->
    val (fileName, taskName) = key
    tasks.register<Download>(taskName) {
        src(it)
        dest(File(destDir, fileName))
    }
}

val downloadSDL3Natives = tasks.register("downloadSDL3Natives") {
    inputs.properties("baseUrl" to baseUrl)
    outputs.dir(destDir)
    nativesUrls.forEach { (key, _) ->
        dependsOn(key.second)
    }
}

sourceSets {
    main {
        resources {
            srcDirs(destDir)
        }
    }
}

tasks {
    build {
        dependsOn(downloadSDL3Natives)
    }
    processResources {
        dependsOn(downloadSDL3Natives)
    }
    named("sourcesJar") {
        dependsOn(downloadSDL3Natives)
    }
}

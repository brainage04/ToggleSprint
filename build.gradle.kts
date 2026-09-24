import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    idea
    java
    id("gg.essential.loom") version "1.15.50"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.gradleup.shadow") version "9.6.1"
    kotlin("jvm") version "2.3.10"
}

//Constants:

val baseGroup: String by project
val mcVersion: String by project
val version: String by project
val mixinGroup = "$baseGroup.mixin"
val modid: String by project

// Toolchains:
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

sourceSets.main {
    java.srcDir(layout.projectDirectory.dir("src/main/kotlin"))
}

// Dependencies:

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://repo.spongepowered.org/maven/")

    maven("https://maven.notenoughupdates.org/releases")
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val shadowModImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val devenvMod: Configuration by configurations.creating {
    isTransitive = false
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    implementation(kotlin("stdlib-jdk8"))

    // If you don't want mixin, remove these lines
    shadowImpl("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        isTransitive = false
    }
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT")
    annotationProcessor("com.google.code.gson:gson:2.14.0")
    annotationProcessor("com.google.guava:guava:33.6.0-jre")
    annotationProcessor("org.ow2.asm:asm-tree:9.10.1")
    annotationProcessor("org.ow2.asm:asm-commons:9.10.1")


    shadowModImpl(libs.moulconfig)
    devenvMod(variantOf(libs.moulconfig) { classifier("test") })

    // bundled and relocated (see shadowJar) so it cannot clash with other mods' Kotlin
    shadowImpl("org.jetbrains.kotlin:kotlin-stdlib:2.3.10") {
        exclude(group = "org.jetbrains", module = "annotations")
    }
    testImplementation(kotlin("test-junit5"))
}

// Minecraft configuration:
loom {
    runs {
        named("client") {
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            programArgs("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
            programArgs("--mods", devenvMod.resolve().joinToString(",") { it.relativeTo(file("run")).path })
        }
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        // If you don't want mixin, remove this lines
        mixinConfig("mixin.$modid.json")
    }
    // If you don't want mixin, remove these lines
    @Suppress("UnstableApiUsage")
    mixin {
        defaultRefmapName.set("mixin.$modid.refmap.json")
    }
}


// Tasks:

tasks.compileJava {
    dependsOn(tasks.processResources)
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType(Jar::class) {
    archiveBaseName.set(modid)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"

        // If you don't want mixin, remove these lines
        this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
        this["MixinConfigs"] = "mixin.$modid.json"
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("mcversion", mcVersion)
    inputs.property("modid", modid)
    inputs.property("mixinGroup", mixinGroup)

    filesMatching(listOf("mcmod.info", "mixin.$modid.json")) {
        expand(inputs.properties)
    }

    rename("(.+_at.cfg)", "META-INF/$1")
}


val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    inputFile.set(tasks.shadowJar.get().archiveFile)
}


tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl, shadowModImpl)
    exclude("META-INF/versions/**")

    // If you want to include other dependencies and shadow them, you can relocate them in here
    relocate("io.github.moulberry.moulconfig", "$baseGroup.deps.moulconfig")
    relocate("kotlin", "$baseGroup.deps.kotlin")
}

tasks.jar {
    archiveClassifier.set("nodeps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.assemble.get().dependsOn(tasks.remapJar)

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
}
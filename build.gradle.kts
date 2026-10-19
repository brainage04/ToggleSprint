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


    shadowModImpl(libs.moulconfig)
    devenvMod(variantOf(libs.moulconfig) { classifier("test") })

    // bundled and relocated (see shadowJar) so it cannot clash with other mods' Kotlin
    shadowImpl("org.jetbrains.kotlin:kotlin-stdlib:2.3.10") {
        exclude(group = "org.jetbrains", module = "annotations")
    }
    testImplementation(kotlin("test-junit5"))
}

// This Forge version predates GameTest. A separate development-only mod checks a real client.
val clientChecks by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output + configurations.compileClasspath.get()
    runtimeClasspath += sourceSets.main.get().output + configurations.runtimeClasspath.get()
}
val clientChecksJar by tasks.registering(Jar::class) {
    archiveClassifier.set("client-checks")
    destinationDirectory.set(layout.buildDirectory.dir("client-check-mod"))
    from(clientChecks.output)
}

// Minecraft configuration:
loom {
    runs {
        named("client") {
            programArgs("--mods", devenvMod.resolve().joinToString(",") { it.relativeTo(file("run")).path })
        }
        create("clientChecks") {
            client()
            runDir("build/client-checks")
            programArgs("--mods", (devenvMod.files + clientChecksJar.get().archiveFile.get().asFile).joinToString(",") { it.absolutePath })
            vmArgs("-Xmx2G")
        }
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
    }
}

// Essential Loom's xvfb wrapper drops the Java classpath. Wrap Gradle with xvfb-run instead.
tasks.withType<net.fabricmc.loom.task.AbstractRunTask>().configureEach {
    useXvfb.set(false)
}

tasks.named("runClientChecks") {
    dependsOn(clientChecksJar)
    val result = layout.buildDirectory.file("client-checks/result.txt")
    doFirst {
        result.get().asFile.delete()
        val options = layout.buildDirectory.file("client-checks/options.txt").get().asFile
        options.parentFile.mkdirs()
        options.writeText("soundCategory_master:0.0\npauseOnLostFocus:false\n")
    }
    doLast {
        check(result.get().asFile.readText().startsWith("PASS")) { "Client checks failed: ${result.get().asFile.readText()}" }
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
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("mcversion", mcVersion)
    inputs.property("modid", modid)

    filesMatching("mcmod.info") {
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
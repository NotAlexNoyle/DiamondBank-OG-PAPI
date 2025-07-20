plugins {
    id("java") // Tell gradle this is a java project.
    id("java-library") // Import helper for source-based libraries.
    kotlin("jvm") version
        "2.1.21" // Import kotlin jvm plugin for kotlin/java integration (Required for DiamondBank-OG API)
    id("com.diffplug.spotless") version "7.0.4" // Import auto-formatter.
    id("com.gradleup.shadow") version "8.3.6" // Import shadow API.
    eclipse // Import eclipse plugin for IDE integration.
}

java {
    sourceCompatibility = JavaVersion.VERSION_17 // Compile with JDK 17 compatibility.

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Use JDK 17.
        vendor.set(JvmVendorSpec.GRAAL_VM) // Use GraalVM CE.
    }
}

kotlin { jvmToolchain(17) }

group = "net.true-og.DiamondBank-OG-PAPI" // Declare bundle identifier.

version = "1.0.7" // Declare plugin version (will be in .jar).

val apiVersion = "1.19" // Declare minecraft server target version.

tasks.named<ProcessResources>("processResources") {
    val props = mapOf("version" to version, "apiVersion" to apiVersion)
    inputs.properties(props) // Indicates to rerun if version changes.
    filesMatching("plugin.yml") { expand(props) }
    from("LICENSE") { into("/") } // Bundle license into .jars.
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://repo.purpurmc.org/snapshots") }
    maven { url = uri("https://maven.lapismc.net/repository/maven/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://repo.helpch.at/releases/") }
}

dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:1.19.4-R0.1-SNAPSHOT") // Declare purpur API version to be packaged.
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnlyApi(project(":libs:DiamondBank-OG")) // Import TrueOG Network DiamondBank-OG API.
}

tasks.withType<AbstractArchiveTask>().configureEach { // Ensure reproducible .jars
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.shadowJar {
    archiveClassifier.set("") // Use empty string instead of null.
    minimize()
}

tasks.build {
    dependsOn(tasks.spotlessApply)
    dependsOn(tasks.shadowJar)
}

tasks.jar { archiveClassifier.set("part") }

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-parameters", "-Xlint:deprecation")) // Triggers deprecation warning messages.
    options.encoding = "UTF-8"
    options.isFork = true
}

spotless {
    java {
        removeUnusedImports()
        palantirJavaFormat()
    }
    kotlinGradle {
        ktfmt().kotlinlangStyle().configure { it.setMaxWidth(120) }
        target("build.gradle.kts", "settings.gradle.kts")
    }
}

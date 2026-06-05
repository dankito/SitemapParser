import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("jvm") version "2.0.21"
}


group = "net.dankito.sitemap"
version = "1.0.0-SNAPSHOT"

ext["customArtifactId"] = "sitemap-parser"

ext["sourceCodeRepositoryBaseUrl"] = "github.com/dankito/SitemapParser"

ext["projectDescription"] = "Discovers and parses sitemap (index) files."


kotlin {
    jvmToolchain(11)

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        javaParameters = true

//        val isRunningInIntelliJ = System.getProperty("idea.active") == "true"
        val isDebuggerPortSet = System.getProperty("debug")?.toIntOrNull() != null
        val isDebuggerAttached = isDebuggerPortSet
                || System.getProperty("idea.debugger.dispatch.addr") != null // does not seem to work anymore

        // avoid "variable has been optimised out" in debugging mode
        if (isDebuggerAttached) {
            freeCompilerArgs.add("-Xdebug")
        }
    }
}


repositories {
    mavenCentral()
}


val jacksonVersion: String = "2.21.2"
val webClientVersion: String = "1.8.0"
val coroutinesVersion: String = "1.9.0"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    implementation("net.dankito.web:web-client-api:$webClientVersion")
    implementation("net.dankito.web:java-http-client-web-client:$webClientVersion")

    implementation("net.codinux.log:klf:1.8.3")


    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
    testImplementation("ch.qos.logback:logback-classic:1.5.32")
}


tasks.test {
    useJUnitPlatform()
}


if (file("./gradle/scripts/publish-dankito.gradle.kts").exists()) {
    apply(from = "./gradle/scripts/publish-dankito.gradle.kts")
}
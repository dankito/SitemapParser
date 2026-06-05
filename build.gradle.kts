plugins {
    kotlin("jvm") version "2.0.21"
}


group = "net.dankito.sitemap"
version = "1.0.0-SNAPSHOT"


kotlin {
    jvmToolchain(21)
}


repositories {
    mavenCentral()
}


val jacksonVersion: String = "2.21.2"
val webClientVersion: String = "1.8.0"

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")

    implementation("net.dankito.web:web-client-api:$webClientVersion")
    implementation("net.dankito.web:java-http-client-web-client:$webClientVersion")

    implementation("net.codinux.log:klf:1.8.3")


    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
    testImplementation("ch.qos.logback:logback-classic:1.5.32")
}


tasks.test {
    useJUnitPlatform()
}
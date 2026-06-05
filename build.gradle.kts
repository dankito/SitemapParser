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

dependencies {
    testImplementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}
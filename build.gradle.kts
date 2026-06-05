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
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
    testImplementation("ch.qos.logback:logback-classic:1.5.32")
}


tasks.test {
    useJUnitPlatform()
}
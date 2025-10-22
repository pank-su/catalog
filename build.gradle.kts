plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "su.pank.transport"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.1"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
}

application {
    mainClass.set("su.pank.transport.TransportRouteManagementApp")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

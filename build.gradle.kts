plugins {
    kotlin("jvm") version "1.3.21"
    maven
}

group = "tomasvolker"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")
}

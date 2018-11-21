import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.0"
}

group = "tomasvolker"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")
}
/*
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
*/
val sourcesJar = tasks.create<Jar>("sourcesJar") {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

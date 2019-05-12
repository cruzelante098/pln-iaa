plugins {
    java
    kotlin("jvm") version "1.3.31"
}

repositories {
    mavenCentral()
    jcenter()
}

fun kotlinx(module: String, version: String? = null): Any =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlinx("coroutines-core", "1.1.1"))
    implementation("com.github.ajalt:clikt:1.7.0")
    implementation("com.opencsv:opencsv:4.5")
    implementation("com.github.ajalt:mordant:1.2.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

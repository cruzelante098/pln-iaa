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
    compile("com.opencsv:opencsv:4.5")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

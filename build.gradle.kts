import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"

}


group = "com.stock"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jsoup:jsoup:1.13.1")

    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-simple:1.7.30")


}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}


tasks.named<Jar>("jar") {
    manifest {
        // attributes(mapOf("Main-Class" to "com.voce.protoss.ProtossMain"))
        attributes["Main-Class"] = "com.stock.MainKt"
        attributes["Gradle-Version"] = "Gradle " + getProject().getGradle().getGradleVersion()
        attributes["Created-By"] = "Java " + JavaVersion.current()
        attributes["Class-Path"] = configurations.runtimeClasspath.get().filter {
            it.name.endsWith(".jar")

        }.joinToString(separator=" ") { "lib/" + it.name }


    }
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

}
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
}

kotlin {
    jvmToolchain(17)
    explicitApi()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
    testImplementation(libs.truth)
}

mavenPublishing {
    configure(
        com.vanniktech.maven.publish.KotlinJvm(
            javadocJar = com.vanniktech.maven.publish.JavadocJar.Dokka("dokkaGenerateHtml"),
            sourcesJar = com.vanniktech.maven.publish.SourcesJar.Sources(),
        ),
    )
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    pom {
        name.set("ShamsiCore")
        description.set(
            "Core Shamsi (Persian/Jalali) date, time, and calendar types. " +
                "Pure-Kotlin, framework-free — usable in Android, JVM, and non-UI layers.",
        )
    }
}

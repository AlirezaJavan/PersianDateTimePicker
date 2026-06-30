plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
}

android {
    namespace = "io.github.alirezajavan.shamsipicker"
    compileSdk = 37

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

kotlin {
    jvmToolchain(17)
    explicitApi()
}

dependencies {
    api(project(":shamsi-core"))
    coreLibraryDesugaring(libs.desugarJdkLibs)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.tooling.preview)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
}

mavenPublishing {
    configure(
        com.vanniktech.maven.publish.AndroidSingleVariantLibrary(
            variant = "release",
            sourcesJar = com.vanniktech.maven.publish.SourcesJar.Sources(),
            javadocJar = com.vanniktech.maven.publish.JavadocJar.Dokka("dokkaGenerateHtml"),
        ),
    )
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    pom {
        name.set("ShamsiPicker")
        description.set("A modern Shamsi (Persian/Jalali) date and time picker for Jetpack Compose.")
    }
}

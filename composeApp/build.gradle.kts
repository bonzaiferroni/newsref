import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"
            }
        }
    }
    
    jvm("desktop")
    task("testClasses")
    
    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.sqldelight.coroutines)

            // Navigator
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.kodein)
            // Screen Model
            // Transitions
            implementation(libs.kodein.di.framework.compose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(project(":chopui"))
            implementation(project(":model"))
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.sqldelight.jvm)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }

    sqldelight {
        databases {
            create("PantryDb") {
                packageName = "streetlight.app"
            }
        }
    }
}

android {
    namespace = "streetlight.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "streetlight.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "streetlight.app"
            packageVersion = "1.0.0"
        }
    }
}

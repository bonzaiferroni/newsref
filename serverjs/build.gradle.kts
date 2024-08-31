import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl

plugins {
    kotlin("multiplatform")
}

group = "streetlight.serverjs"

kotlin {
    js(IR) {
        useCommonJs()
        browser {
            webpackTask {
                mainOutputFileName = "main.bundle.js"
                outputDirectory = File(projectDir.resolve("../www/js").path)
            }
        }
        binaries.executable()
    }
}


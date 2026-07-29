plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    jacoco
}

android {
    namespace = "com.yumedev.taptopayandroid"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yumedev.taptopayandroid"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes +=
                setOf(
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE-notice.md",
                )
        }
    }
    lint {
        abortOnError = true
        warningsAsErrors = false
        checkAllWarnings = true
        checkReleaseBuilds = true
        ignoreTestSources = true
        disable +=
            setOf(
                "ObsoleteLintCustomCheck",
                "OldTargetApi",
                "AndroidGradlePluginVersion",
                "GradleDependency",
                "NewerVersionAvailable",
                "UnsupportedChromeOsHardware",
            )
        warning +=
            setOf(
                "DefaultLocale",
                "RedundantLabel",
            )
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk.android)
    testImplementation(libs.truth)
    testImplementation(libs.androidx.arch.core.testing)

    // Instrumented Testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.truth)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Hilt Testing
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    // Robolectric for unit tests with Android framework
    testImplementation(libs.robolectric)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter =
        listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",
            "**/data/model/*",
            "**/di/**",
            "**/*_HiltModules*",
            "**/*_Factory*",
            "**/*_MembersInjector*",
            "**/Hilt_*",
            "**/*ComposableSingletons*",
            "**/*_Impl*",
        )

    val kotlinClasses = fileTree("${project.layout.buildDirectory.get()}/intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes") {
        exclude(fileFilter)
    }

    val javaClasses = fileTree("${project.layout.buildDirectory.get()}/intermediates/javac/debug/compileDebugJavaWithJavac/classes") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(kotlinClasses, javaClasses))
    executionData.setFrom(
        fileTree(project.layout.buildDirectory.get()) {
            include("jacoco/testDebugUnitTest.exec")
        },
    )
}

tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("jacocoTestReport")

    violationRules {
        rule {
            limit {
                minimum = "0.70".toBigDecimal()
            }
        }

        rule {
            element = "CLASS"
            limit {
                minimum = "0.60".toBigDecimal()
            }
            excludes =
                listOf(
                    "*.di.*",
                    "*.ui.theme.*",
                    "*ComposableSingletons*",
                    "*_HiltModules*",
                    "*_Factory*",
                    "*_Impl*",
                )
        }
    }

    val fileFilter =
        listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",
            "**/data/model/*",
            "**/di/**",
            "**/*_HiltModules*",
            "**/*_Factory*",
            "**/*_MembersInjector*",
            "**/Hilt_*",
            "**/*ComposableSingletons*",
            "**/*_Impl*",
        )

    val kotlinClasses = fileTree("${project.layout.buildDirectory.get()}/intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes") {
        exclude(fileFilter)
    }

    val javaClasses = fileTree("${project.layout.buildDirectory.get()}/intermediates/javac/debug/compileDebugJavaWithJavac/classes") {
        exclude(fileFilter)
    }

    classDirectories.setFrom(files(kotlinClasses, javaClasses))
    executionData.setFrom(
        fileTree(project.layout.buildDirectory.get()) {
            include("jacoco/testDebugUnitTest.exec")
        },
    )
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/config/detekt/detekt.yml")
    baseline = file("$projectDir/config/detekt/baseline.xml")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "17"
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }
}

ktlint {
    android.set(true)
    ignoreFailures.set(false)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

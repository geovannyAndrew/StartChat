import java.time.LocalDate
import java.time.format.DateTimeFormatter
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.kotlinSerialization)
    id("com.google.devtools.ksp")
    id("androidx.room")
}

android {
    namespace = "com.gyros.startchat"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gyros.startchat"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            applicationVariants.all {
                outputs
                    .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                    .forEach { output ->
                        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        output.outputFileName = "start_chat_$date.apk"
                    }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    androidTarget()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinxSerializationJson)
                api(libs.kotlinxDatetime)
                api(libs.multiplatformSettings)
                api(libs.androidxRoomRuntime)
                api(libs.sqliteBundled)
                api(libs.koinCore)
                api(libs.koinCompose)
                api(libs.koinComposeViewmodel)
                api(libs.jetbrainsComposeUi)
                api(libs.jetbrainsComposeUiGraphics)
                api(libs.jetbrainsComposeMaterial3)
                api(libs.jetbrainsComposeMaterialIconsExtended)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidxCoreKtx)
                implementation(libs.androidxLifecycleRuntimeKtx)
                implementation(libs.androidxActivityCompose)
                implementation(libs.androidxUi)
                implementation(libs.androidxUiGraphics)
                implementation(libs.androidxUiToolingPreview)
                implementation(libs.androidxMaterial3)
                implementation(libs.koinAndroid)
                implementation(libs.koinAndroidxCompose)
                implementation(libs.androidxMaterialIconsExtended)
                implementation(libs.androidxNavigationCompose)
                implementation(libs.androidxLifecycleViewmodel)
            }
        }
    }
}

dependencies {
    ksp(libs.androidxRoomCompiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinxCoroutinesTest)
    androidTestImplementation(libs.androidxRoomTesting)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.androidxEspressoCore)
    androidTestImplementation(libs.androidxUiTestJunit4)
    debugImplementation(libs.androidxUiTooling)
    debugImplementation(libs.androidxUiTestManifest)
}

room {
    schemaDirectory("$projectDir/schemas")
}
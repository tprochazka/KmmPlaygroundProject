import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm()
    
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

	        implementation("org.jetbrains.compose.material3.adaptive:adaptive:1.1.2")
	        implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.1.2")
	        implementation("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.1.2")
	        implementation("org.jetbrains.compose.material3:material3-adaptive-navigation-suite:1.9.0")
	        implementation("tech.annexflow.compose:constraintlayout-compose-multiplatform:0.6.1")

	        implementation("co.touchlab:kermit:2.0.8")

	        // https://chrisbanes.github.io/haze/latest/
	        implementation("dev.chrisbanes.haze:haze:1.6.10")

	        //https://github.com/oleksandrbalan/programguide
	        implementation("io.github.oleksandrbalan:programguide:1.6.0")

	        //https://store.mobilenativefoundation.org/docs/quickstart
	        //https://github.com/MayakaApps/Kache
	        //https://github.com/open-tool/ultron
	        //https://github.com/LemonAppDev/konsist
	        //https://github.com/RaedGhazal/kotlinx-datetime-ext


	        // TODO: Just for quick protyping, remove in the future
	        implementation(compose.materialIconsExtended)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "cz.atomsoft.playground.kmm"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "cz.atomsoft.playground.kmm"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "cz.atomsoft.playground.kmm.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "cz.atomsoft.playground.kmm"
            packageVersion = "1.0.0"
        }
    }
}

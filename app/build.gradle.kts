import org.gradle.kotlin.dsl.implementation
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}

android {
    namespace = "com.example.fit2081a1_yang_xingyu_33533563"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fit2081a1_yang_xingyu_33533563"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Load API key from secrets.properties
        val secretsFile = rootProject.file("secrets.properties")
        val secretsProperties = Properties()
        if (secretsFile.exists()) {
            secretsProperties.load(FileInputStream(secretsFile))
            val apiKey = secretsProperties.getProperty("API_KEY", "")
            buildConfigField("String", "API_KEY", "\"$apiKey\"")
        } else {
            buildConfigField("String", "API_KEY", "\"\"")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.jbcrypt)
    implementation(libs.generativeai.vxyz)
    implementation(libs.retrofit)
    implementation(libs.converter.scalars)
    implementation (libs.converter.gson)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.room.runtime)
    implementation(libs.generativeai)
    ksp(libs.room.compiler)
    implementation(libs.androidx.core.splashscreen)
    annotationProcessor(libs.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation (libs.androidx.lifecycle.viewmodel.compose.v271)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core.android)
    implementation(libs.androidx.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.compose.markdown)
}
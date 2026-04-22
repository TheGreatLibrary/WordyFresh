import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    kotlin("plugin.serialization") version "2.0.0"
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

val localProperties = File(rootDir, "local.properties")
val props = Properties().apply {
    if (localProperties.exists()) {
        load(localProperties.inputStream())
    }
}

android {
    namespace = "com.sinya.projects.wordle"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sinya.projects.wordle"
        minSdk = 26
        targetSdk = 36
        versionCode = 19
        versionName = "2.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"${props.getProperty("SUPABASE_URL") ?: ""}\""
        )
        buildConfigField(
            "String",
            "SUPABASE_API_KEY",
            "\"${props.getProperty("SUPABASE_API_KEY") ?: ""}\""
        )
        buildConfigField(
            "String",
            "HINTS_SALT",
            "\"${props.getProperty("HINTS_SALT") ?: ""}\""
        )
        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"${props.getProperty("WEB_CLIENT_ID") ?: ""}\""
        )
    }

    packagingOptions {
        resources {
            excludes += "META-INF/versions/9/previous-compilation-data.bin"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
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

    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    // splashScreen
    implementation(libs.androidx.core.splashscreen)

    // coil для загрузки изображений
    implementation(libs.coil.compose)

    // supabase
    implementation(platform(libs.bom))
    implementation(libs.postgrest.kt)
    implementation(libs.auth.kt)
    implementation(libs.storage.kt)
    implementation(libs.realtime.kt)

    // google
    implementation(libs.google.id)
    implementation(libs.credentials)
    implementation(libs.credentials.play)

    // ktor
    implementation(libs.ktor.client.okhttp)

    // room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // datastore
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    // Ui-тесты
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
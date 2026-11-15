plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.oscar.timesup"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.oscar.timesup"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    // Firma con la clave de Oscar (keystore/alafrente.jks.enc (misma clave que ¡A la frente!), se descifra en GitHub Actions).
    // Si no hay clave disponible (p. ej. compilando en local), se usa la clave debug.
    val ksFile = System.getenv("KEYSTORE_FILE")
    signingConfigs {
        if (ksFile != null) {
            create("oscar") {
                storeFile = file(ksFile)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = "alafrente"
                keyPassword = System.getenv("KEYSTORE_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = if (ksFile != null) signingConfigs.getByName("oscar")
                            else signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.webkit:webkit:1.12.1")
}

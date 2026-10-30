plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ec.edu.monster"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "ec.edu.climov_eurekabank_soapjava_gr09"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "SOAP_URL", "\"${providers.gradleProperty("soapUrl").orElse("http://10.0.2.2:8080/WS_EurekaBank_SOAPJAVA_GR09/WSEurekaBank").get()}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.okhttp)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.navigation.compose)

    // JAXB + Activation (SIN CONFLICTOS)
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("com.sun.xml.bind:jaxb-impl:2.3.1") {
        exclude(group = "javax.activation", module = "javax.activation-api")
    }
    implementation("javax.activation:activation:1.1.1")

    // kSOAP2 (SIN CONFLICTOS)
    implementation("com.google.code.ksoap2-android:ksoap2-android:3.6.4") {
        exclude(group = "javax.activation", module = "javax.activation-api")
    }

    implementation("androidx.compose.material:material-icons-extended:1.6.8")
    implementation(libs.androidx.compose.ui.text)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("com.google.dagger:hilt-android:2.51")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// EXCLUSIÓN GLOBAL DE javax.activation-api
configurations.all {
    exclude(group = "javax.activation", module = "javax.activation-api")
}

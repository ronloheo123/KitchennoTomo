// ❶ IMPORT
import java.util.Properties

plugins { alias(libs.plugins.android.application) }

// ❷ Đọc key: ưu tiên -P/gradle.properties; nếu không có thì đọc local.properties
val openAiKey: String = (project.findProperty("OPENAI_API_KEY") as String?)?.trim()
    ?: run {
        val props = Properties()
        val f = rootProject.file("local.properties")
        if (f.exists()) f.inputStream().use { props.load(it) }
        props.getProperty("OPENAI_API_KEY", "")
    }

android {
    namespace = "com.example.kitchennotomo"
    compileSdk = 36            // ← dùng 34 nếu bạn chưa cài SDK 36; có 36 thì đổi cả 2 dòng về 36

    defaultConfig {
        applicationId = "com.example.kitchennotomo"
        minSdk = 24
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ❸ Sinh hằng cho code Java dùng
        buildConfigField("String", "OPENAI_API_KEY", "\"TEST_KEY\"")
        vectorDrawables.useSupportLibrary = true
    }

    // ❹ Bật sinh BuildConfig
    buildFeatures { buildConfig = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

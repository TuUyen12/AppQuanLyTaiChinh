pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven("https://jitpack.io")
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "7.0.4" apply false // Thay đổi phiên bản nếu cần
        id("com.google.gms.google-services") version "4.4.2" apply false
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // Thêm JitPack repository nếu cần
    }
}

rootProject.name = "QuanLyTaiChinh"
include(":app")
 
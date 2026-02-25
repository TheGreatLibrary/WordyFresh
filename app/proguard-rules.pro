# === Jetpack Compose ===
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keep class kotlin.Metadata { *; }
-keep class androidx.compose.runtime.** { *; }
-keepclasseswithmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# === Room Database ===
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keepclassmembers class * {
    @androidx.room.* <fields>;
}

# === DataStore ===
-keep class androidx.datastore.** { *; }
-keepclassmembers class com.sinya.projects.wordle.data.local.datastore.DataStoreManager { *; }
-dontwarn androidx.datastore.**

# === Ktor/Supabase/Serialization (если используешь kotlinx.serialization) ===
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# === Supabase DTO-модели ===
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**


# Защищаем sealed классы маршрутов
-keep class com.sinya.projects.wordle.navigation.ScreenRoute { *; }

# === Optional: keep line numbers in crash logs ===
-keepattributes SourceFile,LineNumberTable
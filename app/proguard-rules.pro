# Add project specific ProGuard rules here.
-keepclassmembers class * extends androidx.lifecycle.ViewModel { <init>(...); }
-dontwarn kotlinx.**

# Conscrypt TLS provider
-keep class org.conscrypt.** { *; }
-dontwarn org.conscrypt.**

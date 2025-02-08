-ignorewarnings

-keep class com.reveny.nativecheck.app.Native { *; }

-keep class com.reveny.nativecheck.app.DetectionData { *; }

-keep class com.reveny.nativecheck.ui.activity.MainActivity { *; }

-keepclassmembers class * extends androidx.fragment.app.Fragment {
    public ** requireContext(...);
    public ** *(...);
}

-keepclassmembers class * extends androidx.fragment.app.Fragment {
    void *(android.os.Bundle);
    void *(...);
}

# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepnames class androidx.navigation.fragment.NavHostFragment
-keep class com.puntogris.blint.model.Client
-keep class com.puntogris.blint.model.Supplier
-keep class com.puntogris.blint.model.ProductWithSuppliersCategories
-keep class com.puntogris.blint.model.Record
-keep class com.puntogris.blint.model.Event
-keep class com.puntogris.blint.model.UserData
-keep class com.puntogris.blint.model.Employee
-keep class com.puntogris.blint.model.Product
-keep class com.puntogris.blint.model.OrderWithRecords
-keep class com.puntogris.blint.model.Debt
-keep class com.puntogris.blint.model.Category
-keep class com.puntogris.blint.model.FirestoreSupplier

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

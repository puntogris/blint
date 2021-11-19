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
#-keepnames class androidx.navigation.fragment.NavHostFragment
-keep class com.puntogris.blint.model.Client
-keep class com.puntogris.blint.model.Supplier
-keep class com.puntogris.blint.model.product.ProductWithDetails
-keep class com.puntogris.blint.model.order.Record
-keep class com.puntogris.blint.model.Event
-keep class com.puntogris.blint.model.AuthUser
-keep class com.puntogris.blint.model.Business
-keep class com.puntogris.blint.model.product.Product
-keep class com.puntogris.blint.model.order.OrderWithRecords
-keep class com.puntogris.blint.model.Debt
-keep class com.puntogris.blint.model.Category
-keep class com.puntogris.blint.model.FirestoreSupplier


-keeppackagenames javax.**
-keeppackagenames org.apache.poi.**
-keeppackagenames org.apache.poi.ss.formula.function
-keeppackagenames org.openxmlformats.**
-keeppackagenames org.openxmlformats.schemas.**

-keep class javax.** {*;}
-keep class org.apache.poi.** {*;}
-keep class org.apache.xmlbeans.** {*;}
-keep class com.fasterxml.** {*;}
-keep class com.microsoft.schemas.** {*;}

-keep class org.openxmlformats.** {*;}
-keep class org.openxmlformats.schemas.** {*;}
-keep class schemaorg_apache_xmlbeans.** {*;}
-keep class schemasMicrosoftComVml.** {*;}
-keep class schemasMicrosoftComOfficeExcel.** {*;}
-keep class schemasMicrosoftComOfficeOffice.** {*;}

-keepclasseswithmembers class javax.** {*;}
-keepclasseswithmembers class org.apache.poi.** {*;}
-keepclasseswithmembers class org.apache.xmlbeans.** {*;}
-keepclasseswithmembers class com.fasterxml.** {*;}
-keepclasseswithmembers class com.microsoft.schemas.** {*;}

-keepclasseswithmembers class org.openxmlformats.** {*;}
-keepclasseswithmembers class org.openxmlformats.schemas.** {*;}
-keepclasseswithmembers class schemaorg_apache_xmlbeans.** {*;}
-keepclasseswithmembers class schemasMicrosoftComVml.** {*;}
-keepclasseswithmembers class schemasMicrosoftComOfficeExcel.** {*;}
-keepclasseswithmembers class schemasMicrosoftComOfficeOffice.** {*;}

-keep class org.w3c.** {*;}
-keep class org.dom4j.** {*;}
-keep class org.etsi.** {*;}
-keep class com.graphbuilder.** {*;}
-dontwarn org.etsi.**
-dontnote com.microsoft.schemas.**
-dontnote com.graphbuilder.**
-dontwarn org.openxmlformats.**
-dontwarn org.w3c.**
-dontwarn org.dom4j.**
-dontwarn schemasMicrosoftComVml.**
-dontwarn schemasMicrosoftComOfficeExcel.**
-dontwarn schemasMicrosoftComOfficeOffice.**
-dontwarn schemasMicrosoftComOfficeWord.**
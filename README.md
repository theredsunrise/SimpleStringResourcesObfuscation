# Simple String Resources Obfuscation

This project demonstrates how to **obfuscate string resources for Android applications and libraries**.
By obfuscating text, we mean that plain text will not be readable, so **we will encrypt it**.
Static texts in the app can help **hackers** quickly find ways to crack the app or understand how it works. 
For example, **searching for text in login dialogs when the wrong password is entered, or when looking for API keys**.
This methodology **just slows the hacker down** from cracking the app, but does not stop them.
The first step in encoding resource strings is to **clone the working source set for the `develop` build type** into 
the release source set for the `obfuscate` build type. During the cloning of the `develop` source set into `obfuscate`, 
all resource strings without the `skip="true"` parameter are **encrypted using a Gradle script kotlin function**. So obfuscating string resources is done by encrypting 
them and then encoding them into Base64 format to ensure that the encrypted text does not contain special characters 
for the XML format. For this purpose, the **convention plugins** `obfuscation.application` and `obfuscation.library` have been 
created in the `buildSrc` project. Next, it is necessary to ensure that in the release `obfuscate` source set, the encrypted string 
resources are decrypted at runtime. For this, the **native Android library `nativeLib`** is used, which contains a **C++ function** for decrypting. 
This library is automatically added as a dependency in the `obfuscation.application` and `obfuscation.library` plugins. 
For string resources that we want to hide from attackers, **we use the following helper functions stored in the `nativeLib` library**:

```kotlin
// Use for string.
val String.decrypt: String
    get() = specific(com.example.nativelib.BuildConfig.DEVELOP, develop = {
        //Development mode returns the plaintext.
        return this
    }) {
        //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
        NativeLib.decrypt(this)
    }

// Use for string resource id.
fun Context.decrypt(@StringRes id: Int): String =
    specific(com.example.nativelib.BuildConfig.DEVELOP, develop = {
        //Development mode returns the plaintext.
        return getString(id)
    }) {
        //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
        getString(id).decrypt
    }

// Use in view databinding.
object Helper {
    fun decrypt(string: String): String =
        specific(com.example.nativelib.BuildConfig.DEVELOP, develop = {
            //Development mode returns the plaintext.
            return string
        }) {
            //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
            return string.decrypt
        }
}
```

### Usage examples are as follows:
- If we have a `Context`:
  ```kotlin  
  val text = inflater.context.decrypt(R.string.fragment_slideshow_title)
  ```
- For databinding in XML layouts, the `Helper` class:
  ```kotlin
  NavHeaderMainBinding.bind(binding.navView.getHeaderView(0)).also {
  it.helper = Helper
  }
  android:contentDescription="@{helper.decrypt(@string/nav_header_desc)}"
  ```
- For a resource already as an encrypted `String`:
  ```kotlin
  menu.forEach {
  it.title = it.title?.toString()?.let { text -> text.decrypt }
  }
  ```
**For string resources that we do not want to obfuscate**, we can use the `skip="true"` parameter in the `strings.xml` file:
```xml
<string skip="true" name="app_name">String Resources Obfuscation</string>
```
### Used features:
- Gradle convention plugins in buildSrc and code for encrypting string resources
- Native Android library with C++ code and communication via JNI for decrypting string resources

**To sign the `obfuscate` project**, you need to add your own `keystore.jks` file and fill in the
following properties in the `keystore.properties` file:

```
storePassword=...
keyPassword=...
keyAlias=...
```
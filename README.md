# Simple String Resources Obfuscation

This project demonstrates a simple method for obfuscating constant strings in the code of Android
modules (app, library). The approach is based on creating a generated source set `obfuscate` with
modified code derived from the `develop` source set, which is intended for regular development.
Various code modifications for the `obfuscate` source set are handled using Gradle scripts. In this
case, string resources in the format `<string ...>...</string>` are encrypted. Elements that should
not be encrypted by the scripts can be marked with the skip attribute:
`<string skip="true" ...>...</string>`.

For demonstration purposes, `Base64` encryption has been used. The only requirement for developers
is
that strings in the `develop` source code that need to be obfuscated must use the following helper
functions:

```kotlin
// Use for string.
val String.decrypt: String
    get() = specific(com.example.obfuscation.library.BuildConfig.DEVELOP, develop = {
        //Development mode returns the plaintext.
        return this
    }) {
        //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
        Obfuscation.decrypt(this)
    }

// Use for string resource id.
fun Context.decrypt(@StringRes id: Int): String =
    specific(com.example.obfuscation.library.BuildConfig.DEVELOP, develop = {
        //Development mode returns the plaintext.
        return getString(id)
    }) {
        //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
        getString(id).decrypt
    }

// Use in view databinding.
object Helper {
    fun decrypt(string: String): String = specific(BuildConfig.DEVELOP, develop = {
        //Development mode returns the plaintext.
        return string
    }) {
        //Obfuscate mode returns the decrypted value of a string resource that was encrypted earlier with Gradle during the build process.
        return string.decrypt
    }
}
```

And all obfuscated strings must be referenced from string resources. The concept of generated source
code also allows for the creation of macros, which can be replaced with specific functions in Gradle
scripts. However, this has not been implemented here as this is only a demonstration.

To sign the `obfuscate` project, you need to add your own `keystore.jks` file and fill in the
following properties in the `keystore.properties` file:

```
storePassword=...
keyPassword=...
keyAlias=...
```

The class responsible for encryption is located in the directory
`buildSrc/src/main/kotlin/com/example/shared/Obfuscation.kt`. It is shared between Gradle scripts
and
source codes. The directory `buildSrc/src/main/kotlin/com/example/shared/buildScript` contains files
exclusively for Gradle scripts (precompiled scripts and source codes).
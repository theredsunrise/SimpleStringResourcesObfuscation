import java.io.FileInputStream
import java.util.Properties

Properties().apply {
    val keystorePropertiesRelativePath: String by project
    load(FileInputStream(file(keystorePropertiesRelativePath)))
    val keystoreProperties by extra(this)
}
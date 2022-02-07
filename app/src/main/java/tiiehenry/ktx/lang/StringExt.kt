package tiiehenry.ktx.lang

import java.io.File

fun String.fileExtension(): String {
    return File(this).extension
}
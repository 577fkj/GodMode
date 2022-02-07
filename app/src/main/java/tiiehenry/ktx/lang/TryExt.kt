package tiiehenry.ktx.lang


fun Any?.trySafe(f: (Any) -> Unit) {
    if (this != null)
        try {
            f.invoke(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
}
inline fun <T> T.tryApply(block: T.() -> Unit): T {
    if (this != null)
        try {
            block()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    return this
}

inline fun <T, R> T.tryLet(block: (T) -> R): R? {
    if (this != null)
        try {
           return block(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    return null
}
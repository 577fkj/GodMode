package tiiehenry.ktx.lang


inline fun <R> CharSequence.ifBlank(block: (CharSequence) -> R) {
    if (this.isBlank())
        block.invoke(this)
}

inline fun <R> CharSequence.ifNotBlank(block: (CharSequence) -> R) {
    if (this.isNotBlank())
        block.invoke(this)
}

inline fun <R> CharSequence.ifEmpty(block: (CharSequence) -> R) {
    if (this.isEmpty())
        block.invoke(this)
}

inline fun <R> CharSequence.ifNotEmpty(block: (CharSequence) -> R) {
    if (this.isNotEmpty())
        block.invoke(this)
}

inline fun <R> Boolean.ifFalse(block: (Boolean) -> R) {
    if (this == false)
        block.invoke(this)
}

inline fun <R> Boolean.ifTrue(block: (Boolean) -> R) {
    if (this)
        block.invoke(this)
}

//@Deprecated("no auto infer", ReplaceWith("if (v == null) block"))
inline fun <T, R> T.ifNull(v: Any?, block: (T) -> R) {
    if (v == null)
        block.invoke(this)
}

//@Deprecated("no auto infer", ReplaceWith("if (v != null) block"))
inline fun <T, R> T.ifNonNull(v: Any?, block: (T) -> R) {
    if (v != null)
        block.invoke(this)
}
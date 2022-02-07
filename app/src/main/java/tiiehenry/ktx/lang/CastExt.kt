package tiiehenry.ktx.lang

fun Any?.asString(): String? {
    return this as? String?
}

fun Any?.asString(f: (String) -> Unit) {
    asString()?.let { f.invoke(it) }
}

fun Any?.asInt(): Int? {
    return this as? Int?
}

fun Any?.asInt(f: (Int) -> Unit) {
    asInt()?.let { f.invoke(it) }
}

fun Any?.asInteger(): Int? {
    return this as? Int?
}

fun Any?.asInteger(f: (Int) -> Unit) {
    asInteger()?.let { f.invoke(it) }
}

fun Any?.asBoolean(): Boolean? {
    return this as? Boolean?
}

fun Any?.asBoolean(f: (Boolean) -> Unit) {
    asBoolean()?.let { f.invoke(it) }
}

fun Any?.asArray(): Array<*>? {
    return this as? Array<*>?
}

fun Any?.asArray(f: (Array<*>) -> Unit) {
    asArray()?.let { f.invoke(it) }
}
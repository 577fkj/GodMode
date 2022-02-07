package com.lxj.androidktx.core

import android.util.Log
import com.lxj.androidktx.AndroidConfig

/**
 * Description: log相关，日志的开关和默认tag通过AndroidConfig来配置
 * Create by lxj, at 2018/12/5
 */

private enum class LogLevel{
    Verbose, Debug, Info, Warn, Error
}

@Deprecated(message = "推荐使用logv")
fun String.v(tag: String = AndroidConfig.defaultLogTag){
    intervalLog(LogLevel.Verbose, tag, this )
}

fun Any.logv(tag: String, msg: String){
    intervalLog(LogLevel.Verbose, tag, msg )
}
fun Any.logv(msg: String){
    intervalLog(LogLevel.Verbose, AndroidConfig.defaultLogTag, msg )
}

@Deprecated(message = "推荐使用logd")
fun String.d(tag: String = AndroidConfig.defaultLogTag){
    intervalLog(LogLevel.Debug, tag, this )
}

fun Any.logd(tag: String, msg: String){
    intervalLog(LogLevel.Debug, tag, msg )
}
fun Any.logd(msg: String){
    intervalLog(LogLevel.Debug, AndroidConfig.defaultLogTag, msg )
}

@Deprecated(message = "推荐使用logi")
fun String.i(tag: String = AndroidConfig.defaultLogTag){
    intervalLog(LogLevel.Info, tag, this )
}

fun Any.logi(tag: String, msg: String){
    intervalLog(LogLevel.Info, tag, msg )
}
fun Any.logi(msg: String){
    intervalLog(LogLevel.Info, AndroidConfig.defaultLogTag, msg )
}

@Deprecated(message = "推荐使用logw")
fun String.w(tag: String = AndroidConfig.defaultLogTag){
    intervalLog(LogLevel.Warn, tag, this )
}
fun Any.logw(tag: String, msg: String){
    intervalLog(LogLevel.Warn, tag, msg )
}
fun Any.logw(msg: String){
    intervalLog(LogLevel.Warn, AndroidConfig.defaultLogTag, msg )
}

@Deprecated(message = "推荐使用loge")
fun String.e(tag: String = AndroidConfig.defaultLogTag){
    intervalLog(LogLevel.Error, tag, this )
}

fun Any.loge(tag: String, msg: String){
    intervalLog(LogLevel.Error, tag, msg )
}
fun Any.loge(msg: String){
    intervalLog(LogLevel.Error, AndroidConfig.defaultLogTag, msg )
}
fun Any.logea(msg: Any?){
    loge(msg.toString())
}

private fun intervalLog(level: LogLevel, tag: String, msg: String){
    if(AndroidConfig.isDebug){
        when (level) {
            LogLevel.Verbose -> Log.v(tag, msg)
            LogLevel.Debug -> Log.d(tag, msg)
            LogLevel.Info -> Log.i(tag, msg)
            LogLevel.Warn -> Log.w(tag, msg)
            LogLevel.Error -> Log.e(tag, msg)
        }
    }
}

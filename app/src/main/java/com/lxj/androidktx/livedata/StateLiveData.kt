package com.lxj.androidktx.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Description: 携带状态的LiveData
 * Create by lxj, at 2019/3/6
 */
class StateLiveData<T> : NoStickyLiveData<T>() {

    enum class State {
        Idle, Loading, Success, Error, Empty
    }

    val state = MutableLiveData<State>()
    var errMsg = ""

    init {
        clearState()
    }

    fun postValueAndSuccess(value: T) {
        super.postValue(value)
        postSuccess()
    }

    fun clearState() {
        this.errMsg = ""
        state.postValue(State.Idle)
    }

    fun postLoading() {
        state.postValue(State.Loading)
    }

    fun postSuccess() {
        state.postValue(State.Success)
    }

    fun postError() {
        state.postValue(State.Error)
    }

    fun postEmpty() {
        state.postValue(State.Empty)
    }

    fun changeState(s: State) {
        state.postValue(s)
    }


    /**
     * 智能post值，能根据值进行智能的设置自己的状态，无需手工编写代码
     * @param dataValue 目标值，根据目标值去设置对应的state
     */
    fun smartPost(dataValue: T?){
        if(dataValue==null){
            postError()
        }else if(dataValue is Collection<*> && dataValue.isEmpty()){
            postEmpty()
        }else{
            postValueAndSuccess(dataValue)
        }
    }

    /**
     * 强大而实用的封装，启动协程执行逻辑（比如网络请求），并对逻辑结果进行智能post。示例如下：
     * launchAndSmartPost {
     *      "https://iandroid.xyz/api".http().get<T>().await()
     * }
     */
    fun launchAndSmartPost(block: suspend CoroutineScope.() -> T?): Job {
        postLoading()
        return GlobalScope.launch { smartPost(block()) }
    }

}


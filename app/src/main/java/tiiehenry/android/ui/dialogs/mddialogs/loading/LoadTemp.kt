package tiiehenry.android.ui.dialogs.mddialogs.loading

import tiiehenry.android.ui.dialogs.api.callback.OnShowListener
import tiiehenry.android.ui.dialogs.api.strategy.loading.ILoadingTask

class LoadTemp {

    val loadingTaskList = mutableListOf<Pair<CharSequence, ILoadingTask>>()

    var autoExecute: Boolean = true
    var autoDismiss: Boolean = true
    var showListener: OnShowListener? = null
    var minDisplayTime: Long = 0

}
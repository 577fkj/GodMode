package tiiehenry.android.ui.dialogs.mddialogs

import android.app.Activity
import tiiehenry.android.ui.dialogs.api.strategy.Dialogs
import tiiehenry.android.ui.dialogs.mddialogs.confirm.MDConfirmDialogProvider
import tiiehenry.android.ui.dialogs.mddialogs.custom.MDCustomDialogProvider
import tiiehenry.android.ui.dialogs.mddialogs.input.MDInputDialogProvider
import tiiehenry.android.ui.dialogs.mddialogs.list.MDListDialogProvider
import tiiehenry.android.ui.dialogs.mddialogs.loading.MDLoadingDialogProvider
import tiiehenry.android.ui.dialogs.mddialogs.progress.MDProgressDialogProvider

class MDDialogs(val activity: Activity) : Dialogs() {

    init {
        confirmDialogProvider = MDConfirmDialogProvider(activity)
        inputDialogProvider = MDInputDialogProvider(activity)
        loadingDialogProvider = MDLoadingDialogProvider(activity)
        listDialogProvider = MDListDialogProvider(activity)
        customDialogProvider = MDCustomDialogProvider(activity)
        progressDialogProvider = MDProgressDialogProvider(activity)
    }

    fun initGlobal() {
        Dialogs.newInstance()
        Dialogs.setConfirmDialogProvider(MDConfirmDialogProvider(activity))
        Dialogs.setInputDialogProvider(MDInputDialogProvider(activity))
        Dialogs.setLoadingDialogProvider(MDLoadingDialogProvider(activity))
        Dialogs.setListDialogProvider(MDListDialogProvider(activity))
        Dialogs.setCustomDialogProvider(MDCustomDialogProvider(activity))
        Dialogs.setProgressDialogProvider(MDProgressDialogProvider(activity))
    }
}
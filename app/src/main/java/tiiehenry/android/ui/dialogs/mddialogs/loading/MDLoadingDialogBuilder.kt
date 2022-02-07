package tiiehenry.android.ui.dialogs.mddialogs.loading

import android.content.Context
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import tiiehenry.android.ui.dialogs.mddialogs.base.MaterialBaseDialogBuilder
import tiiehenry.android.ui.dialogs.mddialogs.base.NegativeButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.NeutralButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.PositiveButtonTemp
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.callback.OnShowListener
import tiiehenry.android.ui.dialogs.api.strategy.loading.ILoadingDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.loading.ILoadingTask
import tiiehenry.android.ui.dialogs.mddialogs.R

class MDLoadingDialogBuilder(val context: Context) : MaterialBaseDialogBuilder<ILoadingDialogBuilder>, ILoadingDialogBuilder {
    override val builder: MaterialDialog = MaterialDialog(context)

    override val positiveTemp: PositiveButtonTemp = PositiveButtonTemp()
    override val negativeTemp: NegativeButtonTemp = NegativeButtonTemp()
    override val neutralTemp: NeutralButtonTemp = NeutralButtonTemp()
    private val loadTemp: LoadTemp = LoadTemp()

    override lateinit var dialog: IDialog

    override fun minDisplayTime(delay: Long): ILoadingDialogBuilder {
        loadTemp.minDisplayTime = delay
        return builder()
    }

    override fun showListener(listener: OnShowListener): ILoadingDialogBuilder {
        loadTemp.showListener = listener
        return super.showListener(listener)
    }

    override fun autoDismiss(enable: Boolean): ILoadingDialogBuilder {
        loadTemp.autoDismiss = enable
        return super.autoDismiss(enable)
    }

    override fun autoExecuteTask(auto: Boolean): ILoadingDialogBuilder {
        loadTemp.autoExecute = auto
        return builder()
    }

    override fun addLoadingTask(text: CharSequence, task: ILoadingTask): ILoadingDialogBuilder {
        loadTemp.loadingTaskList.add(text to task)
        return builder()
    }

    override fun addLoadingTask(text: Int, task: ILoadingTask): ILoadingDialogBuilder {
        return addLoadingTask(context.getString(text), task)
    }

    override fun build(): IDialog {
        val layout = LayoutInflater.from(context).inflate(R.layout.mddialogs_loading_layout, null)
        builder.customView(view = layout, dialogWrapContent = false)
        dialog = MaterialLoadingDialogWrapper(this, layout, loadTemp)
        return dialog
    }

    override fun builder(): ILoadingDialogBuilder {
        return this
    }

}


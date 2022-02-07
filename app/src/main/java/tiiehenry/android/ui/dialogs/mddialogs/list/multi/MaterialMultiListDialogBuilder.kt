package tiiehenry.android.ui.dialogs.mddialogs.list.multi

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.mddialogs.base.NegativeButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.NeutralButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.PositiveButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.list.base.MaterialBaseListDialogBuilder
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.callback.ListCallbackMultiChoice
import tiiehenry.android.ui.dialogs.api.strategy.list.multi.IMultiListDialogBuilder

class MaterialMultiListDialogBuilder(val context: Context) : MaterialBaseListDialogBuilder<IMultiListDialogBuilder>, IMultiListDialogBuilder {
    override val builder: MaterialDialog = MaterialDialog(context)

    override val positiveTemp: PositiveButtonTemp = PositiveButtonTemp()
    override val negativeTemp: NegativeButtonTemp = NegativeButtonTemp()
    override val neutralTemp: NeutralButtonTemp = NeutralButtonTemp()
    override val listTemp: MultiListTemp = MultiListTemp()

    override lateinit var dialog: IDialog

    override fun itemsCallbackMultiChoice(selectedIndices: IntArray?, callback: ListCallbackMultiChoice): IMultiListDialogBuilder {
        if (selectedIndices != null) {
            listTemp.selectedIndices = selectedIndices
        }
        listTemp.itemCallback = callback
        return builder()
    }

    override fun alwaysCallMultiChoiceCallback(): IMultiListDialogBuilder {
        listTemp.alwaysCallMultiChoiceCallback = true
        return builder()
    }

    override fun builder(): IMultiListDialogBuilder {
        return this
    }


}
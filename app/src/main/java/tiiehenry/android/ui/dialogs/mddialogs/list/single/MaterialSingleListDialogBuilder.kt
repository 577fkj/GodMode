package tiiehenry.android.ui.dialogs.mddialogs.list.single

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.mddialogs.base.NegativeButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.NeutralButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.PositiveButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.list.base.MaterialBaseListDialogBuilder
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.callback.ListCallbackSingleChoice
import tiiehenry.android.ui.dialogs.api.strategy.list.single.ISingleListDialogBuilder

class MaterialSingleListDialogBuilder(val context: Context) : MaterialBaseListDialogBuilder<ISingleListDialogBuilder>, ISingleListDialogBuilder {
    override val builder: MaterialDialog = MaterialDialog(context)

    override val positiveTemp: PositiveButtonTemp = PositiveButtonTemp()
    override val negativeTemp: NegativeButtonTemp = NegativeButtonTemp()
    override val neutralTemp: NeutralButtonTemp = NeutralButtonTemp()
    override val listTemp: SingleListTemp = SingleListTemp()


    override lateinit var dialog: IDialog

    override fun alwaysCallSingleChoiceCallback(): ISingleListDialogBuilder {
        listTemp.alwaysCallSingleChoiceCallback = true
        return builder()
    }


    override fun itemsCallbackSingleChoice(selectedIndex: Int, callback: ListCallbackSingleChoice): ISingleListDialogBuilder {
        listTemp.selectedIndex = selectedIndex
        listTemp.itemCallback = callback
        return builder()
    }

    override fun builder(): ISingleListDialogBuilder {
        return this
    }

}
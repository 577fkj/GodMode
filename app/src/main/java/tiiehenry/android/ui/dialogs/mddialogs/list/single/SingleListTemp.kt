package tiiehenry.android.ui.dialogs.mddialogs.list.single

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import tiiehenry.android.ui.dialogs.mddialogs.list.base.ListTemp
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.callback.ListCallbackSingleChoice

class SingleListTemp : ListTemp() {
    var selectedIndex: Int = -1
    var alwaysCallSingleChoiceCallback: Boolean = false
    var itemCallback: ListCallbackSingleChoice? = null

    override fun apply(builder: MaterialDialog, idialog: IDialog) {
        builder.listItemsSingleChoice(res = itemsRes, items = itemList,
                disabledIndices = disabledIndices,
                initialSelection = selectedIndex,
                waitForPositiveButton = !alwaysCallSingleChoiceCallback) { dialog, index, text ->
            itemCallback?.onSelection(idialog, index, text)
        }
    }
}
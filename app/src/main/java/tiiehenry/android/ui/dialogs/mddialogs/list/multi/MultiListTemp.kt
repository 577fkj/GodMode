package tiiehenry.android.ui.dialogs.mddialogs.list.multi

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import tiiehenry.android.ui.dialogs.mddialogs.list.base.ListTemp
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.callback.ListCallbackMultiChoice

class MultiListTemp : ListTemp() {
    var itemCallback: ListCallbackMultiChoice? = null
    var selectedIndices: IntArray = IntArray(0)
    var alwaysCallMultiChoiceCallback: Boolean = false

    override fun apply(builder: MaterialDialog, idialog: IDialog) {
        builder.listItemsMultiChoice(res = itemsRes, items = itemList,
                disabledIndices = disabledIndices,
                initialSelection = selectedIndices,
                waitForPositiveButton = !alwaysCallMultiChoiceCallback) { dialog, indices, items ->
            itemCallback?.onSelection(idialog, indices, items.toTypedArray())
        }
    }
}
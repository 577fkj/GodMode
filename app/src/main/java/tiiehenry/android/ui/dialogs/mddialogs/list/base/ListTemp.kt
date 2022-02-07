package tiiehenry.android.ui.dialogs.mddialogs.list.base

import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.api.IDialog

abstract class ListTemp {

    var itemsRes: Int? = null
    var itemList: List<CharSequence>? = null
    var disabledIndices: IntArray? = null
    var itemsIdsRes: Int? = null
    var itemsIds: IntArray? = null

    abstract fun apply(builder: MaterialDialog, idialog: IDialog)

}
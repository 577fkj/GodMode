package tiiehenry.android.ui.dialogs.mddialogs.list.base

import tiiehenry.android.ui.dialogs.mddialogs.base.MaterialBaseDialogBuilder
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.base.content.IDialogBaseItems

interface MaterialBaseListDialogBuilder<T> : MaterialBaseDialogBuilder<T>, IDialogBaseItems<T> {
    val listTemp: ListTemp

    override fun itemsIds(idsArray: IntArray): T {
        listTemp.itemsIds = idsArray
        return builder()
    }

    override fun itemsIds(idsArrayRes: Int): T {
        listTemp.itemsIdsRes = idsArrayRes
        return builder()
    }
//md_item_selector
//    override fun listSelector(selectorRes: Int): T {
//        builder.getItemSelector()
//        builder.listSelector(selectorRes)
//        return builder()
//    }

    override fun itemsDisabledIndices(vararg disabledIndices: Int): T {
        listTemp.disabledIndices = intArrayOf(*disabledIndices)
        return builder()
    }

//
//    override fun itemsGravity(gravity: GravityEnum): T {
//        builder.itemsGravity(translateGravityEnum(gravity.gravityInt))
//        return builder()
//    }

    override fun items(itemCollection: MutableCollection<CharSequence>): T {
        return items(itemCollection.toList())
    }

    override fun items(itemList: List<CharSequence>): T {
        listTemp.itemList = itemList
        return builder()
    }

    override fun items(itemsRes: Int): T {
        listTemp.itemsRes = itemsRes
        return builder()
    }

    override fun items(vararg items: CharSequence): T {
        return items(items.toList())
    }

    override fun build(): IDialog {
        val dialog = super.build()
        listTemp.apply(builder, dialog)
        return dialog
    }
}
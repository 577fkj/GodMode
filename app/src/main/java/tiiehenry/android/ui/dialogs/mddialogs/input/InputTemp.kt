package tiiehenry.android.ui.dialogs.mddialogs.input

import android.text.InputType
import tiiehenry.android.ui.dialogs.api.callback.InputCallback
import tiiehenry.android.ui.dialogs.api.callback.button.ButtonCallback

class InputTemp {
    var hint: CharSequence? = null
    var preFill: CharSequence? = null
    var allowEmptyInput: Boolean? = null
    var alwaysCallInputCallback: Boolean = false
    var inputType: Int = InputType.TYPE_CLASS_TEXT
    var inputRange: Pair<Int, Int>? = null
    var callback: InputCallback? = null

    var res: Int? = null
    var text: CharSequence? = null
    var click: ButtonCallback? = null


}
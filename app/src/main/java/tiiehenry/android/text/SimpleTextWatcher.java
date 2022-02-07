package tiiehenry.android.text;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleTextWatcher implements TextWatcher {
    public BeforeWatcher beforeWatcher;
    public AfterWatcher afterWatcher;
    public OnWatcher onWatcher;

    public static SimpleTextWatcher newBeforeWatcher(BeforeWatcher beforeWatcher) {
        return new SimpleTextWatcher(beforeWatcher);
    }

    public static SimpleTextWatcher newOnWatcher(OnWatcher onWatcher) {
        return new SimpleTextWatcher(onWatcher);
    }

    public static SimpleTextWatcher newAfterWatcher(AfterWatcher afterWatcher) {
        return new SimpleTextWatcher(afterWatcher);
    }

    public SimpleTextWatcher(BeforeWatcher beforeWatcher) {
        this.beforeWatcher = beforeWatcher;
    }

    public SimpleTextWatcher(OnWatcher onWatcher) {
        this.onWatcher = onWatcher;
    }

    public SimpleTextWatcher(AfterWatcher afterWatcher) {
        this.afterWatcher = afterWatcher;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (beforeWatcher != null)
            beforeWatcher.beforeTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (onWatcher != null)
            onWatcher.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (afterWatcher != null)
            afterWatcher.afterTextChanged(s.toString());
    }

    public interface BeforeWatcher {
        void beforeTextChanged(CharSequence s, int start, int count, int after);
    }

    public interface OnWatcher {
        void onTextChanged(CharSequence s, int start, int before, int count);
    }

    public interface AfterWatcher {
        void afterTextChanged(String s);
    }

}

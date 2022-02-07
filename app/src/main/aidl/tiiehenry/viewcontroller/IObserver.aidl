package tiiehenry.viewcontroller;

import tiiehenry.viewcontroller.rule.ActRules;

interface IObserver {
    void onEditModeChanged(boolean enable);
    void onAppStatusChanged( boolean enable);
    void onViewRuleChanged(String packageName, in ActRules actRules);
}

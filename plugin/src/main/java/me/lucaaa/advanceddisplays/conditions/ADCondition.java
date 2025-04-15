package me.lucaaa.advanceddisplays.conditions;

import me.lucaaa.advanceddisplays.api.conditions.Condition;

public abstract class ADCondition implements Condition {
    protected boolean isCorrect = true;

    public boolean isCorrect() {
        return isCorrect;
    }
}
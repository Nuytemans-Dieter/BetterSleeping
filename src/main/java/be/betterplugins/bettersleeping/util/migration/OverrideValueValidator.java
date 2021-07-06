package be.betterplugins.bettersleeping.util.migration;

import be.dezijwegel.betteryaml.validation.validator.Validator;
import org.jetbrains.annotations.NotNull;

public class OverrideValueValidator extends Validator
{

    private final Object forcedValue;

    public OverrideValueValidator(Object forcedValue)
    {
        this.forcedValue = forcedValue;
    }

    @Override
    public Object validate(@NotNull Object o)
    {
        return forcedValue != null ? forcedValue : o;
    }
}

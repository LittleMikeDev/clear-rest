package uk.co.littlemike.clearrest;

import java.util.Optional;

public class AnnotatedMethodParameter {
    public final String name;
    private final int index;
    private final Optional<String> defaultValue;

    AnnotatedMethodParameter(int index, String name, Optional<String> defaultValue) {
        this.index = index;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public Object getValueFromArguments(Object[] argumentValues) {
        Object value = argumentValues[index];
        if (value == null) {
            return defaultValue.orElseThrow(() -> new IllegalArgumentException(String.format(
                    "URI parameter %s with no @DefaultValue cannot be null", name
            )));
        }
        return value;
    }
}

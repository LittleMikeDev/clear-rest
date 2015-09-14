package uk.co.littlemike.clearrest;

import javax.ws.rs.DefaultValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;

public abstract class AnnotatedMethodParameters<A extends Annotation> {

    protected final List<AnnotatedMethodParameter> parameters = new ArrayList<>();

    protected AnnotatedMethodParameters(
            Class<A> parameterAnnotationType,
            Function<A, String> parameterNameExtractor,
            Method method) {

        int argIndex = 0;
        for (Parameter parameter : method.getParameters()) {
            A parameterAnnotation = parameter.getAnnotation(parameterAnnotationType);
            DefaultValue defaultValueAnnotation = parameter.getAnnotation(DefaultValue.class);

            if (parameterAnnotation != null) {
                Optional<String> defaultValue = Optional.ofNullable(defaultValueAnnotation).map(DefaultValue::value);
                String name = parameterNameExtractor.apply(parameterAnnotation);
                parameters.add(new AnnotatedMethodParameter(argIndex++, name, defaultValue));
            }
        }
    }

    public Map<String, Object> mapValues(Object[] argumentValues) {
        Map<String, Object> valueMap = new HashMap<>();
        for (AnnotatedMethodParameter parameter : parameters) {
            valueMap.put(parameter.name, parameter.getValueFromArguments(argumentValues));
        }
        return valueMap;
    }
}

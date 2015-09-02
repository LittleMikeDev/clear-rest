package uk.co.littlemike.hallo;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

public class ResourceMethod {
    private static class Parameter {
        public final int index;
        public final String name;

        private Parameter(int index, String name) {
            this.index = index;
            this.name = name;
        }
    }

    private static class PathParameters {
        private final List<Parameter> parameters = new ArrayList<>();

        public void add(int index, String name) {
            parameters.add(new Parameter(index, name));
        }

        public Map<String, Object> mapValues(Object[] argumentValues) {
            Map<String, Object> valueMap = new HashMap<>();
            for (Parameter parameter : parameters) {
                valueMap.put(parameter.name, argumentValues[parameter.index]);
            }
            return valueMap;
        }
    }

    private final Optional<String> methodPath;
    private final PathParameters pathParameters = new PathParameters();

    public ResourceMethod(Method method) {
        methodPath = Optional.ofNullable(method.getAnnotation(Path.class)).map(Path::value);

        for (int argIndex = 0; argIndex < method.getParameterCount(); argIndex++) {
            PathParam pathParam = annotationFromArgument(method, argIndex, PathParam.class);
            if (pathParam != null) {
                pathParameters.add(argIndex, pathParam.value());
            }
        }
    }

    public URI uriFromArguments(Object[] arguments, UriBuilder baseUri) {
        methodPath.ifPresent(baseUri::path);
        return baseUri.buildFromMap(pathParameters.mapValues(arguments));
    }

    private static <T extends Annotation> T annotationFromArgument(Method method, int argIndex, Class<T> annotation) {
        return method.getParameters()[argIndex].getAnnotation(annotation);
    }
}

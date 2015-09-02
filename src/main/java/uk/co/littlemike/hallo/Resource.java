package uk.co.littlemike.hallo;

import javax.ws.rs.*;
import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class Resource {
    private static final List<? super Annotation> METHOD_ANNOTATIONS = Arrays.asList(
            Path.class, GET.class, PUT.class, POST.class, DELETE.class, HEAD.class, OPTIONS.class
    );

    private final Class<?> resourceClass;

    public Resource(Class<?> resourceClass) {
        this.resourceClass = resourceClass;
    }

    public boolean isValidResourceClass() {
        return isPathAnnotated()
                || hasResourceMethod();
    }

    private boolean isPathAnnotated() {
        return resourceClass.isAnnotationPresent(Path.class);
    }

    private boolean hasResourceMethod() {
        return Arrays.stream(resourceClass.getMethods())
                .flatMap(m -> Arrays.stream(m.getAnnotations()))
                .map(Annotation::annotationType)
                .anyMatch(METHOD_ANNOTATIONS::contains);
    }

    public UriBuilder uriFromBase(UriBuilder baseUri) {
        if (isPathAnnotated()) {
            return baseUri.path(resourceClass);
        }
        return baseUri;
    }
}

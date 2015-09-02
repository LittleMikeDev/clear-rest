package uk.co.littlemike.hallo;

import uk.co.littlemike.gotcha.Invocation;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;

public class ResourceMethodInvocation {

    private final Method method;

    public ResourceMethodInvocation(Invocation invocation) {
        method = invocation.getMethod();
    }

    public UriBuilder pathFromBase(UriBuilder baseUri) {
        if (method.isAnnotationPresent(Path.class)) {
            return baseUri.path(method);
        }
        return baseUri;
    }
}

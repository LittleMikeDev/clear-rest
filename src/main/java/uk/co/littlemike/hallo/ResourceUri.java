package uk.co.littlemike.hallo;

import uk.co.littlemike.gotcha.InvocationCaptor;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Consumer;

public class ResourceUri<R> {

    public static <R> ResourceUri<R> toResource(Class<R> resourceClass, UriInfo uriInfo) {
        return new ResourceUri<>(resourceClass, uriInfo.getBaseUriBuilder());
    }

    private final InvocationCaptor<R> invocationCaptor;
    private final UriBuilder resourceUri;

    private ResourceUri(Class<R> resourceClass, UriBuilder baseUri) {
        Resource resource = new Resource(resourceClass);
        if (!resource.isValidResourceClass()) {
            throw new IllegalArgumentException();
        }

        resourceUri = resource.uriFromBase(baseUri);
        invocationCaptor = InvocationCaptor.forClass(resourceClass);
    }

    public URI forMethod(Consumer<R> methodInvocation) {
        ResourceMethodInvocation invocation = new ResourceMethodInvocation(invocationCaptor.capture(methodInvocation));
        return invocation.pathFromBase(resourceUri.clone()).build();
    }
}

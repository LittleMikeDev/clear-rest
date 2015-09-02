package uk.co.littlemike.hallo;

import uk.co.littlemike.gotcha.Invocation;
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
            throw new IllegalArgumentException("Not a valid ");
        }

        resourceUri = resource.uriFromBase(baseUri);
        invocationCaptor = InvocationCaptor.forClass(resourceClass);
    }

    public URI forMethod(Consumer<R> methodInvocation) {
        Invocation invocation = invocationCaptor.capture(methodInvocation);
        ResourceMethod method = new ResourceMethod(invocation.getMethod());
        return method.uriFromArguments(invocation.getArguments(), resourceUri.clone());
    }
}

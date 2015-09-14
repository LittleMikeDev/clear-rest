package uk.co.littlemike.clearrest;

import uk.co.littlemike.gotcha.Invocation;
import uk.co.littlemike.gotcha.InvocationCaptor;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;

public class ResourceUri<R> {

    public static <R> ResourceUri<R> toResource(Class<R> resourceClass, UriInfo uriInfo) {
        return new ResourceUri<>(resourceClass, uriInfo);
    }

    private final InvocationCaptor<R> invocationCaptor;
    private final UriBuilder resourceUri;

    private ResourceUri(Class<R> resourceClass, UriInfo uriInfo) {
        Objects.requireNonNull(resourceClass, "Resource class must not be null");
        Objects.requireNonNull(uriInfo, "URI info must not be null");

        Resource resource = new Resource(resourceClass);
        resourceUri = resource.uriFromBase(uriInfo.getBaseUriBuilder());
        invocationCaptor = InvocationCaptor.forClass(resourceClass);
    }

    public URI method(Consumer<R> methodInvocation) {
        Invocation invocation = invocationCaptor.capture(methodInvocation);
        ResourceMethod method = new ResourceMethod(invocation.getMethod());
        return method.uriFromArguments(invocation.getArguments(), resourceUri.clone());
    }
}

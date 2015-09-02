package uk.co.littlemike.hallo;

import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceUriTest {

    public static class NotAResource {
        @SuppressWarnings("unused")
        public void get() {}
    }

    public static class RootResource {
        @GET
        public void get() {}
    }

    @Path("resource")
    public static class Resource {
        @GET
        public void get() {}

        @Path("sub-resource")
        public void getSubResource() {}
    }

    private final UriInfo uriInfo = new StubUriInfo();

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfClassIsNotAResource() {
        ResourceUri.toResource(NotAResource.class, uriInfo);
    }

    @Test
    public void canBuildUriForRootResourceWithoutPaths() {
        ResourceUri.toResource(RootResource.class, uriInfo).forMethod(RootResource::get);
    }

    @Test
    public void canBuildUriForResources() {
        ResourceUri<Resource> uri = ResourceUri.toResource(Resource.class, uriInfo);
        assertThat(uri.forMethod(Resource::getSubResource).toString()).endsWith("/resource/sub-resource");
        assertThat(uri.forMethod(Resource::get).toString()).endsWith("/resource");
    }
}

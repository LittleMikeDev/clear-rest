package uk.co.littlemike.hallo;

import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.UriInfo;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceUriTest {

    public static class NotAResource {
        @SuppressWarnings("unused")
        public void get() {}
    }

    public static class RootResource {
        @GET
        public void get() {}

        public static ResourceUri<RootResource> uri(UriInfo uriInfo) {
            return ResourceUri.toResource(RootResource.class, uriInfo);
        }
    }

    @Path("resource")
    public static class Resource {
        @GET
        public void get() {}

        @Path("sub-resource")
        public void getSubResource() {}

        public static ResourceUri<Resource> uri(UriInfo uriInfo) {
            return ResourceUri.toResource(Resource.class, uriInfo);
        }
    }

    @Path("resource/{id}")
    public static class ResourceWithPathParams {

        @Path("sub-resource/{id2}")
        public void getSubResource(@PathParam("id") String id, @PathParam("id2") String id2) {}

        public static ResourceUri<ResourceWithPathParams> uri(UriInfo uriInfo) {
            return ResourceUri.toResource(ResourceWithPathParams.class, uriInfo);
        }
    }

    private final UriInfo uriInfo = new StubUriInfo();

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfClassIsNotAResource() {
        ResourceUri.toResource(NotAResource.class, uriInfo);
    }

    @Test
    public void canBuildUriForRootResourceWithoutPaths() {
        RootResource.uri(uriInfo).forMethod(RootResource::get);
    }

    @Test
    public void canBuildUriForResources() {
        ResourceUri<Resource> uri = Resource.uri(uriInfo);
        assertThat(uri.forMethod(Resource::getSubResource).toString()).endsWith("/resource/sub-resource");
        assertThat(uri.forMethod(Resource::get).toString()).endsWith("/resource");
    }

    @Test
    public void substitutesPathParameters() {
        ResourceUri<ResourceWithPathParams> uri = ResourceWithPathParams.uri(uriInfo);

        assertThat(uri.forMethod(r -> r.getSubResource("1", "2")).toString())
                .endsWith("/resource/1/sub-resource/2");
    }
}

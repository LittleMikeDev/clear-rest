package uk.co.littlemike.clearrest;

import org.junit.Test;

import javax.ws.rs.*;
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

        public void getWithQueryParams(
                @QueryParam("param1") String param1,
                @DefaultValue("123") @QueryParam("param2") Integer param2) {}

    }

    @Path("resource/{id}")
    public static class ResourceWithPathParams {

        @Path("sub-resource/{id2}")
        public void getSubResource(@PathParam("id") String id, @PathParam("id2") String id2) {}

    }

    private final UriInfo uriInfo = new StubUriInfo();

    @Test(expected = NullPointerException.class)
    public void throwsExceptionIfResourceClassIsNull() {
        ResourceUri.toResource(null, uriInfo);
    }

    @Test(expected = NullPointerException.class)
    public void throwsExceptionIfUriInfoIsNull() {
        ResourceUri.toResource(Resource.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfClassIsNotAResource() {
        ResourceUri.toResource(NotAResource.class, uriInfo);
    }

    @Test
    public void canBuildUriForRootResourceWithoutPaths() {
        ResourceUri.toResource(RootResource.class, uriInfo)
                .method(RootResource::get);
    }

    @Test
    public void canBuildUriForResources() {
        ResourceUri<Resource> resourceUri = ResourceUri.toResource(Resource.class, uriInfo);

        assertThat(resourceUri.method(Resource::getSubResource).toString()).endsWith("/resource/sub-resource");
        assertThat(resourceUri.method(Resource::get).toString()).endsWith("/resource");
    }

    @Test
    public void substitutesPathParameters() {
        URI uri = ResourceUri
                .toResource(ResourceWithPathParams.class, uriInfo)
                .method(r -> r.getSubResource("1", "2"));

        assertThat(uri.toString()).endsWith("/resource/1/sub-resource/2");
    }

    @Test
    public void includesQueryParameters() {
        URI uri = ResourceUri
                .toResource(Resource.class, uriInfo)
                .method(r -> r.getWithQueryParams("a", 2));

        assertThat(uri.toString()).endsWith("?param1=a&param2=2");
    }

    @Test
    public void usesDefaultQueryParameterIfValueNotSet() {
        URI uri = ResourceUri
                .toResource(Resource.class, uriInfo)
                .method(r -> r.getWithQueryParams("abc", null));

        assertThat(uri.toString()).endsWith("?param1=abc&param2=123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfQueryParameterNotSetAndNoDefaultValue() {
        ResourceUri.toResource(Resource.class, uriInfo)
                .method(r -> r.getWithQueryParams(null, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfPathParameterNotSet() {
        ResourceUri.toResource(ResourceWithPathParams.class, uriInfo)
                .method(r -> r.getSubResource(null, null));
    }
}

# Clear-rest

[![Download](https://api.bintray.com/packages/littlemikedev/maven/clear-rest/images/download.svg) ](https://bintray.com/littlemikedev/maven/clear-rest/_latestVersion)
[![Build Status](https://travis-ci.org/LittleMikeDev/clear-rest.svg)](https://travis-ci.org/LittleMikeDev/clear-rest)
[![codecov.io](http://codecov.io/github/LittleMikeDev/clear-rest/coverage.svg?branch=master)](http://codecov.io/github/LittleMikeDev/clear-rest?branch=master)

A library for building type-safe links to JAX-RS resources using Java8 lambdas 

## Benefits

Work resource links in the same way as any other plain old java method!

* Never generate a broken link
* Search for resource method usages using IDE
* Freely change between query-params and path-params
* Change parameter names
* Refactor method signatures safely using IDE refactoring

## How do I use it?

Annotate your resources with JAX-RS annotations

```
@Path("departments")
public class DepartmentsResource {
    
    @GET
    public Response getAll() {
        //...
    }
    
    @GET
    public Response findByName(@QueryParam("name") String name, @DefaultValue("1") @QueryParam("page") Integer page) {
        //...
    }
    
    @GET
    @Path("{departmentId}/employees/{employeeId}")
    public Response getEmployee(@PathParam("departmentId") int departmentId, @PathParam("employeeId") int employeeId) {
        //...
    }
}
```

Use ResourceUri to build type-safe links without worrying about the format

```
    ResourceUri<DepartmentsResource> resource = ResourceUri.toResource(DepartmentsResource.class, uriInfo);

    URI getAllUri = resource.method(r -> r.getAll());
    URI findUri = resource.method(r -> r.findByName("somename", null));
    URI employeeUri = resource.method(r -> r.getEmployee(123, 456));
    
    assertThat(getAllUri.toString()).endsWith("/departments");
    assertThat(findUri.toString()).endsWith("/departments?name=somename&page=1");
    assertThat(employeeUri.toString()).endsWith("/departments/123/employees/456");
```


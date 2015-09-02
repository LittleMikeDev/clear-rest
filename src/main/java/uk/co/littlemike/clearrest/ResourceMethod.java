package uk.co.littlemike.clearrest;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

public class ResourceMethod {

    private static class PathParameters extends AnnotatedMethodParameters<PathParam> {
        public PathParameters(Method method) {
            super(PathParam.class, PathParam::value, method);
        }
    }

    private static class QueryParameters extends AnnotatedMethodParameters<QueryParam> {
        public QueryParameters(Method method) {
            super(QueryParam.class, QueryParam::value, method);
        }

        private void setValuesOnUri(UriBuilder baseUri, Object[] argumentValues) {
            for (Map.Entry<String, Object> param : mapValues(argumentValues).entrySet()) {
                baseUri.queryParam(param.getKey(), param.getValue());
            }
        }
    }

    private final Optional<String> annotatedMethodPath;
    private final PathParameters annotatedPathParameters;
    private final QueryParameters annotatedQueryParameters;

    public ResourceMethod(Method method) {
        annotatedMethodPath = Optional.ofNullable(method.getAnnotation(Path.class)).map(Path::value);
        annotatedPathParameters = new PathParameters(method);
        annotatedQueryParameters = new QueryParameters(method);
    }

    public URI uriFromArguments(Object[] argumentValues, UriBuilder baseUri) {
        annotatedMethodPath.ifPresent(baseUri::path);
        annotatedQueryParameters.setValuesOnUri(baseUri, argumentValues);
        return baseUri.buildFromMap(annotatedPathParameters.mapValues(argumentValues));
    }

}

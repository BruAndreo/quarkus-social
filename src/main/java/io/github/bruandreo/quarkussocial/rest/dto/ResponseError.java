package io.github.bruandreo.quarkussocial.rest.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ResponseError {

    public static final int UNPROCESSABLE_REQUEST_STATUS = 422;

    private String message;

    private Collection<FieldError> fieldErrors;


    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations) {
        var errors = violations
                .stream()
                .map(it -> new FieldError(it.getPropertyPath().toString(), it.getMessage()))
                .collect(Collectors.toList());

        return new ResponseError("Validation Error", errors);
    }

    public Response withStatusCode(int code) {
        return Response.status(code).entity(this).build();
    }

}

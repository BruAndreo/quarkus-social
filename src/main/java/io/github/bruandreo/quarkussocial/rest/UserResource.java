package io.github.bruandreo.quarkussocial.rest;

import io.github.bruandreo.quarkussocial.domain.model.User;
import io.github.bruandreo.quarkussocial.domain.repository.UserRepository;
import io.github.bruandreo.quarkussocial.rest.dto.CreateUserRequest;
import io.github.bruandreo.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserRepository repository;
    private final Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest) {
        var violations = validator.validate(userRequest);

        if (!violations.isEmpty()) {
            return ResponseError
                    .createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_REQUEST_STATUS);
        }

        var user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        repository.persist(user);

        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    public Response listAllUsers() {
        var users = repository.findAll().list();

        return Response.ok(users).build();
    }

    @GET
    @Path("{id}")
    public Response getUserById(@PathParam("id") Long id) {
        var user = getUser(id);

        if (user != null) {
            return Response.ok(user).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUserById(@PathParam("id") Long id, CreateUserRequest userRequest) {
        var user = getUser(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        return Response.ok(user).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUserById(@PathParam("id") Long id) {
        var user = getUser(id);
        repository.delete(user);

        return Response.ok().build();
    }

    private User getUser(Long id) {
        return repository.findById(id);
    }

}

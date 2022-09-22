package io.github.bruandreo.quarkussocial.rest;

import io.github.bruandreo.quarkussocial.domain.model.User;
import io.github.bruandreo.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest) {

        var user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        user.persist();

        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers() {
        var users = User.findAll().list();

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
        user.persist();

        return Response.ok(user).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUserById(@PathParam("id") Long id) {
        var user = getUser(id);
        user.delete();

        return Response.ok().build();
    }

    private User getUser(Long id) {
        return User.findById(id);
    }

}

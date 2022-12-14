package io.github.bruandreo.quarkussocial.rest;

import io.github.bruandreo.quarkussocial.domain.model.Follower;
import io.github.bruandreo.quarkussocial.domain.repository.FollowerRepository;
import io.github.bruandreo.quarkussocial.domain.repository.UserRepository;
import io.github.bruandreo.quarkussocial.rest.dto.FollowerPerUserResponse;
import io.github.bruandreo.quarkussocial.rest.dto.FollowerRequest;
import io.github.bruandreo.quarkussocial.rest.dto.FollowersResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowersResource {

    private FollowerRepository repository;
    private UserRepository userRepository;

    @Inject
    public FollowersResource(FollowerRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request) {
        if (userId.equals(request.getFollowerId())) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("You can't follow yourself")
                    .build();
        }

        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var follower = userRepository.findById(request.getFollowerId());

        boolean follows = repository.follows(follower, user);

        if (!follows) {
            var entity = new Follower();
            entity.setFollower(follower);
            entity.setUser(user);
            repository.persist(entity);
        }

        return Response.ok().build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {
        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = repository.findFollowersByUser(userId);
        FollowerPerUserResponse objectResponse = new FollowerPerUserResponse();
        objectResponse.setFollowersCount(list.size());

        var followersList = list.stream()
                .map(FollowersResponse::new)
                .collect(Collectors.toList());

        objectResponse.setContent(followersList);

        return Response.ok(objectResponse).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        repository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}

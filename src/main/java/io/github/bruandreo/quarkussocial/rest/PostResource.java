package io.github.bruandreo.quarkussocial.rest;

import io.github.bruandreo.quarkussocial.domain.model.Post;
import io.github.bruandreo.quarkussocial.domain.model.User;
import io.github.bruandreo.quarkussocial.domain.repository.FollowerRepository;
import io.github.bruandreo.quarkussocial.domain.repository.PostRepository;
import io.github.bruandreo.quarkussocial.domain.repository.UserRepository;
import io.github.bruandreo.quarkussocial.rest.dto.CreatePostRequest;
import io.github.bruandreo.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;

    @Inject
    public PostResource(
            UserRepository userRepository,
            PostRepository postRepository,
            FollowerRepository followerRepository
    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = this.userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);

        this.postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId) {
        if (followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Follower Id is required").build();
        }

        User follower = userRepository.findById(followerId);
        if (follower == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Follower Id not exist").build();
        }

        User user = this.userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        boolean follows = followerRepository.follows(follower, user);

        if (!follows) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        var postList = postRepository.find(
                "user",
                Sort.by("datatime", Sort.Direction.Ascending),
                user
        ).list();

        var list = postList.stream().map(PostResponse::fromEntity).collect(Collectors.toList());

        return Response.ok(list).build();
    }
}

package io.github.bruandreo.quarkussocial.rest;

import io.github.bruandreo.quarkussocial.domain.model.Follower;
import io.github.bruandreo.quarkussocial.domain.model.Post;
import io.github.bruandreo.quarkussocial.domain.model.User;
import io.github.bruandreo.quarkussocial.domain.repository.FollowerRepository;
import io.github.bruandreo.quarkussocial.domain.repository.PostRepository;
import io.github.bruandreo.quarkussocial.domain.repository.UserRepository;
import io.github.bruandreo.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
public class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    User user;
    User userNotFollower;
    User userFollower;

    private User createUser(String name, int age) {
        var user = new User();
        user.setAge(age);
        user.setName(name);
        userRepository.persist(user);

        return user;
    }

    @BeforeEach
    @Transactional
    public void setUp() {
        user = createUser("Fulano", 30);
        userNotFollower = createUser("Fulanin", 25);
        userFollower = createUser("Fulanezio", 20);

        Post post = new Post();
        post.setText("test");
        post.setUser(user);
        postRepository.persist(post);

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest() {
        var postResquest = new CreatePostRequest();
        postResquest.setText("Some text");

        given()
            .contentType(ContentType.JSON)
            .body(postResquest)
            .pathParam("userId", user.getId())
        .when()
            .post()
        .then()
            .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when userId not exists")
    public void userIdNotExistsTest() {
        var postResquest = new CreatePostRequest();
        postResquest.setText("Some text");

        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .body(postResquest)
            .pathParam("userId", inexistentUserId)
        .when()
            .get()
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 when followerId header is not present")
    public void listPostFollowerHearderNotSendTest() {
        given()
            .pathParam("userId", user.getId())
        .when()
            .get()
        .then()
            .statusCode(400)
            .body(Matchers.is("Follower Id is required"));
    }

    @Test
    @DisplayName("Should return 400 when follower not exists")
    public void listPostFollowerNotFoundTest() {
        var inexsistentFollowerId = 999;

        given()
            .pathParam("userId", user.getId())
            .header("followerId", inexsistentFollowerId)
        .when()
            .get()
        .then()
            .statusCode(400)
            .body(Matchers.is("Follower Id not exist"));
    }

    @Test
    @DisplayName("Should return 404 when user not exists")
    public void listPostUserNotFoundTest() {
        var inexistentUserId = 999;

        given()
            .pathParam("userId", inexistentUserId)
            .header("followerId", userFollower.getId())
        .when()
            .get()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should return 403 when follower doesn't follow the user")
    public void listPostFollowerNotFollowUserTest() {
        given()
            .pathParam("userId", user.getId())
            .header("followerId", userNotFollower.getId())
        .when()
            .get()
        .then()
            .statusCode(403);
    }


    @Test
    @DisplayName("Should return Posts")
    public void listPostTest() {
        given()
            .pathParam("userId", user.getId())
            .header("followerId", userFollower.getId())
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", Matchers.is(1));
    }

}

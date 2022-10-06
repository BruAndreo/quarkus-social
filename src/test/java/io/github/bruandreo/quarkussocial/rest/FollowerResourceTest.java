package io.github.bruandreo.quarkussocial.rest;

import io.github.bruandreo.quarkussocial.domain.model.Follower;
import io.github.bruandreo.quarkussocial.domain.model.User;
import io.github.bruandreo.quarkussocial.domain.repository.FollowerRepository;
import io.github.bruandreo.quarkussocial.domain.repository.UserRepository;
import io.github.bruandreo.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(FollowersResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    User user;
    Long userId;

    User follower;
    Long followerId;

    private User createUser(String name, int age) {
        var user = new User();
        user.setAge(age);
        user.setName(name);
        userRepository.persist(user);

        return user;
    }

    @BeforeEach
    @Transactional
    void setUp() {
        user = createUser("Fulano", 30);
        follower = createUser("Fulaninho", 20);

        userId = user.getId();
        followerId = follower.getId();

        var followEntity = new Follower();
        followEntity.setFollower(follower);
        followEntity.setUser(user);
        followerRepository.persist(followEntity);
    }

    @Test
    @DisplayName("should return 409 when followerId is equal to userId")
    @Order(1)
    public void sameUserAsFollowerTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(409)
            .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("should return 404 when userId not exist")
    @Order(2)
    public void userNotFoundWhenTryingToFollowTest() {
        var inexistentUserId = 999;
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", inexistentUserId)
        .when()
            .put()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should follow an user")
    @Order(3)
    public void followUserTest() {
        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("should return 404 on list user followers an user when userId not exist")
    @Order(4)
    public void userNotFoundWhenListFollowersTest() {
        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", inexistentUserId)
        .when()
            .get()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should list an users followers")
    @Order(5)
    public void listUserFollowersTest() {

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
        .when()
            .get()
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("should return 404 on unfollow an user when userId not exist")
    @Order(6)
    public void userNotFoundWhenUnfollowUserTest() {
        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", inexistentUserId)
            .queryParam("followerId", followerId)
        .when()
            .get()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should unfollow an user")
    @Order(6)
    public void unfollowUserTest() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .queryParam("followerId", followerId)
        .when()
            .get()
        .then()
            .statusCode(200);
    }

}

package io.github.bruandreo.quarkussocial.rest;

import io.github.bruandreo.quarkussocial.rest.dto.CreateUserRequest;
import io.github.bruandreo.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserResourceTest {

    @Test
    @DisplayName("should create an user successfully")
    public void createUserTest() {
        var user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(20);

        var response =
            given()
                .contentType(ContentType.JSON)
                .body(user)
            .when()
                .post("/users")
            .then()
                .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("should return error when body is not valid")
    public void createUserValidationError() {
        var user = new CreateUserRequest();
        user.setName(null);

        var response =
                given()
                    .contentType(ContentType.JSON)
                    .body(user)
                .when()
                    .post("/users")
                .then()
                    .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_REQUEST_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("fieldErrors");
        assertNotNull(errors.get(0).get("message"));
    }
}

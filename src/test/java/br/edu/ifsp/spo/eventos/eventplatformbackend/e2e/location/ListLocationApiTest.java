package br.edu.ifsp.spo.eventos.eventplatformbackend.e2e.location;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ListLocationApiTest {
    @LocalServerPort
    private int localPort;
    private String locationsURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        locationsURI = "/api/v1/locations";
    }
    @Test
    @DisplayName("GET /locations - empty list")
    @Sql("/sql/delete_all_tables.sql")
    public void getAllLocationsEmptyList() {
        given()
            .contentType(ContentType.JSON)
            .log().all()
        .when()
            .get(locationsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("GET /locations - one location inside list")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    public void getListOneLocations() {
        given()
            .contentType(ContentType.JSON)
            .log().all()
        .when()
            .get(locationsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(1));
    }

    @Test
    @DisplayName("GET /locations - multiples location inside list")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    public void getListMultiplesLocations() {
        given()
            .contentType(ContentType.JSON)
            .log().all()
        .when()
            .get(locationsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(3));
    }
}

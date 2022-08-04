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


// https://www.javadoc.io/doc/io.rest-assured/rest-assured/4.5.1/index.html
// https://github.com/rest-assured/rest-assured/wiki/Usage
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LocationDetailsApiTest {
    @LocalServerPort
    private int localPort;
    private String locationURI;
    private String locationAreasURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        locationURI = "/api/v1/locations/{locationId}";
        locationAreasURI = "/api/v1/locations/{locationId}/areas";
    }

    @Test
    @DisplayName("GET /locations/{locationId}")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    public void getLocation() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .log().all()
        .when()
            .get(locationURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("IFSP Campus São Paulo"))
                .body("address", equalTo("Rua Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"));
    }

    @Test
    @DisplayName("GET /locations/{locationId} - not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    public void getLocationNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
            .log().all()
        .when()
            .get(locationURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("GET /locations/{locationId}/areas - empty list")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    public void getAllAreasEmptyList() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .log().all()
        .when()
            .get(locationAreasURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("GET /locations/{locationId}/areas  - one area inside list")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    public void getListOneArea() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .log().all()
        .when()
                .get(locationAreasURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(1));
    }

    @Test
    @DisplayName("GET /locations/{locationId}/areas  - multiple areas inside list")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    @Sql("/sql/areas/insert_many.sql")
    public void getListMultipleAreas() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .log().all()
        .when()
            .get(locationAreasURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(2));
    }
}

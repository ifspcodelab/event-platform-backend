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
public class EditLocationApiTest {
    @LocalServerPort
    private int localPort;
    private String locationURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        locationURI = "/api/v1/locations/{locationId}";
    }
    @Test
    @DisplayName("PUT /locations/{locationId}")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    public void putLocations() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .body("""
                {
                   "name": "IFSP Campus SP",
                   "address": "Rua Pedro Vicente, 100 - Canindé, São Paulo - SP, 01109-010"
                }
            """)
            .log().all()
        .when()
            .put(locationURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("IFSP Campus SP"))
                .body("address", equalTo("Rua Pedro Vicente, 100 - Canindé, São Paulo - SP, 01109-010"));
    }

    @Test
    @DisplayName("PUT /locations/{locationId} - address only")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    public void putLocationsChangeAddressOnly() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .body("""
                {
                   "name": "IFSP Campus São Paulo",
                   "address": "Rua Pedro Vicente, 100 - Canindé, São Paulo - SP, 01109-010"
                }
            """)
            .log().all()
        .when()
            .put(locationURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("IFSP Campus São Paulo"))
                .body("address", equalTo("Rua Pedro Vicente, 100 - Canindé, São Paulo - SP, 01109-010"));

    }


    @Test
    @DisplayName("PUT /locations/{locationId} - already exists")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    public void putLocationsAlreadyExists() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .body("""
                {
                   "name": "Shopping D",
                   "address": "Rua Pedro Vicente, 100 - Canindé, São Paulo - SP, 01109-010"
                }
            """)
            .log().all()
        .when()
            .put(locationURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Resource already exists exception"))
                .body("violations", hasSize(1));

    }

    @Test
    @DisplayName("PUT /locations/{locationId} - not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    public void putLocationsNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "2e07b7bd-b4ab-46fc-b19c-b3425b668b3b")
            .body("""
                {
                   "name": "Shopping D",
                   "address": "Rua Pedro Vicente, 100 - Canindé, São Paulo - SP, 01109-010"
                }
            """)
            .log().all()
        .when()
            .put(locationURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }
}

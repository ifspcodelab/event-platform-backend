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
public class CreateLocationApiTest {
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
    @DisplayName("POST /locations")
    @Sql("/sql/delete_all_tables.sql")
    public void postLocations() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                   "name": "IFSP Campus São Paulo",
                   "address": "Rua Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
                }
            """)
            .log().all()
        .when()
            .post(locationsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("IFSP Campus São Paulo"))
                .body("address", equalTo("Rua Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"));

    }

    @Test
    @DisplayName("POST /locations - already exists")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    public void postLocationsAlreadyExists() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                   "name": "IFSP Campus São Paulo",
                   "address": "Rua Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
                }
            """)
            .log().all()
        .when()
            .post(locationsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Resource already exists exception"))
                .body("violations", hasSize(1));

    }
}

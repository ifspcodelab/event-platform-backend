package br.edu.ifsp.spo.eventos.eventplatformbackend.e2e.area;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateAreaApiTest {
    @LocalServerPort
    private int localPort;
    private String areasURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        areasURI = "/api/v1/locations/{locationId}/areas";
    }

    @Test
    @DisplayName("POST /locations/{locationId}/areas")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    public void postAreas() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .body("""
                {
                     "name": "Bloco A",
                     "reference": "Perto da entrada"
                }
            """)
            .log().all()
        .when()
            .post(areasURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Bloco A"))
                .body("reference", equalTo("Perto da entrada"))
                .body("location.id", notNullValue())
                .body("location.name", equalTo("IFSP Campus São Paulo"))
                .body("location.address", equalTo("Rua Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"));
    }

    @Test
    @DisplayName("POST /locations/{locationId}/areas - already exists")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    @Sql("/sql/areas/insert_many.sql")
    public void postAreasAlreadyExists() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .body("""
                {
                    "name": "Bloco H",
                    "reference": "Perto da entrada"
                }
            """)
            .log().all()
        .when()
            .post(areasURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Resource already exists exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("POST /locations/{locationId}/areas - Location not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    public void postAreasLocationNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3385")
            .body("""
                {
                    "name": "Bloco A",
                    "reference": "Perto da entrada"
                }
            """)
            .log().all()
        .when()
            .post(areasURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }

}

package br.edu.ifsp.spo.eventos.eventplatformbackend.e2e.space;

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
public class CreateSpaceApiTest {
    @LocalServerPort
    private int localPort;
    private String spacesURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        spacesURI = "api/v1/locations/{locationId}/areas/{areaId}/spaces";
    }

    @Test
    @DisplayName("POST /locations/{locationId}/areas/{areaId}/spaces")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    public void postSpaces() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .body("""
                 {
                    "name": "Sala 308",
                    "capacity": 15,
                    "type": "LABORATORY"
                 }
            """)
            .log().all()
        .when()
            .post(spacesURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Sala 308"))
                .body("capacity", equalTo(15))
                .body("type",equalTo("LABORATORY"))
                .body("area.id", notNullValue())
                .body("area.name", equalTo("Bloco A"))
                .body("area.reference", equalTo("Perto da entrada"))
                .body("area.location.id", notNullValue())
                .body("area.location.name", equalTo("IFSP Campus São Paulo"))
                .body("area.location.address", equalTo("Rua Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"));
    }

    @Test
    @DisplayName("POST /locations/{locationId}/areas/{areaId}/spaces - already exists")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    @Sql("/sql/spaces/insert_one.sql")
    public void postSpacesAlreadyExists() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
                .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
                .body("""
                {
                    "name": "Sala 308",
                    "capacity": 20,
                    "type":"CLASSROOM"
                }
            """)
                .log().all()
                .when()
                .post(spacesURI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(409)
                .body("title", equalTo("Resource already exists exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("POST /locations/{locationId}/areas/{areaId}/spaces - Location not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    @Sql("/sql/areas/insert_many.sql")
    public void postSpacesLocationNotFound() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
                .pathParam("areaId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
                .body("""
                {
                    "name": "Sala 308",
                    "capacity": 15,
                    "type": "LABORATORY"
                 }
            """)
                .log().all()
                .when()
                .post(spacesURI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }
}

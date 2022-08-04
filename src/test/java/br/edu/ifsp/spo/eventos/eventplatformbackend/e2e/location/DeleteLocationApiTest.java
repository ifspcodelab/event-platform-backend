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
public class DeleteLocationApiTest {
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
    @DisplayName("DELETE /locations/{locationId}")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    public void deleteLocation() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .log().all()
        .when()
            .delete(locationURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(204);
    }

    @Test
    @DisplayName("DELETE /locations/{locationId} - not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    public void deleteLocationNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
            .log().all()
        .when()
            .delete(locationURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("DELETE /locations/{locationId} - with areas associated")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    public void deleteLocationWithAreasAssociated() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .log().all()
        .when()
            .delete(locationURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Resource referential integrity exception"))
                .body("violations", hasSize(2));
    }
}

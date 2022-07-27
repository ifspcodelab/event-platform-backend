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
public class DeleteAreaApiTest {
    @LocalServerPort
    private int localPort;
    private String areaURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        areaURI = "/api/v1/locations/{locationId}/areas/{areaId}";
    }

    @Test
    @DisplayName("DELETE /locations/{locationId}/areas/{areaId}")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    @Sql("/sql/areas/insert_many.sql")
    public void deleteArea() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .log().all()
        .when()
            .delete(areaURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(204);
    }

    @Test
    @DisplayName("DELETE /locations/{locationId}/areas/{areaId} - not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    @Sql("/sql/areas/insert_many.sql")
    public void deleteAreaNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
            .log().all()
        .when()
             .delete(areaURI)
        .then()
             .log().all()
             .assertThat()
             .statusCode(404)
             .body("title", equalTo("Resource not found exception"))
             .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("DELETE /locations/{locationId}/areas/{areaId} - with spaces associated")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    @Sql("/sql/spaces/insert_one.sql")
    public void deleteAreaWithSpacesAssociated() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .log().all()
        .when()
            .delete(areaURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Resource referential integrity exception"))
                .body("violations", hasSize(2));
    }
}

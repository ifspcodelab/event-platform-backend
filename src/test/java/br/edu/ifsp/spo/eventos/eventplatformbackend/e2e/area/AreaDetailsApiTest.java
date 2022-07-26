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
public class AreaDetailsApiTest {
    @LocalServerPort
    private int localPort;
    private String areasURI;
    private String areaURI;
    private String areaSpacesURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        areasURI = "/api/v1/locations/{locationId}/areas";
        areaURI = "/api/v1/locations/{locationId}/areas/{areaId}";
        areaSpacesURI = "/api/v1/locations/{locationId}/areas/{areaId}/spaces";
    }

    @Test
    @DisplayName("GET /locations/{locationId}/areas - empty list")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    public void getAllEmptyList() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .log().all()
        .when()
            .get(areasURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("GET /locations/{locationId}/areas/{areaId} ")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    public void getArea() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .log().all()
        .when()
            .get(areaURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("Bloco A"))
                .body("reference", equalTo("Perto da entrada"))
                .body("location.id", notNullValue())
                .body("location.name", equalTo("IFSP Campus São Paulo"))
                .body("location.address", equalTo("Rua Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"));
    }

    @Test
    @DisplayName("GET /locations/{locationId}/areas/{areaId} - not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    public void getAreaNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
            .log().all()
        .when()
            .get(areaURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("GET /locations/{locationId}/areas/{areaId}/spaces - empty list")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    public void getAllAreaSpacesEmptyList() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .log().all()
        .when()
            .get(areaSpacesURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(0));
    }

}

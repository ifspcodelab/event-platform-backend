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
public class SpaceDetailsApiTest {
    @LocalServerPort
    private int localPort;
    private String spaceURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        spaceURI = "api/v1/locations/{locationId}/areas/{areaId}/spaces/{spaceId}";
    }

    @Test
    @DisplayName("GET /locations/{locationId}/areas/{areaId}/spaces/{spaceId} ")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    @Sql("/sql/spaces/insert_one.sql")
    public void getSpace() {
        given()
             .contentType(ContentType.JSON)
             .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
             .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
             .pathParam("spaceId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
            .log().all()
        .when()
            .get(spaceURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
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
    @DisplayName("GET /locations/{locationId}/areas/{areaId}/spaces/{spaceId} - not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    @Sql("/sql/spaces/insert_one.sql")
    public void getSpaceNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .pathParam("spaceId", "a6118369-b18b-48e9-b172-ba23be91d9d5")
            .log().all()
        .when()
            .get(spaceURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }
}

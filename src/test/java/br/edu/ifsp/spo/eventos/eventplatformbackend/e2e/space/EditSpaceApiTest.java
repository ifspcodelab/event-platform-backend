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
public class EditSpaceApiTest {
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
    @DisplayName("PUT /locations/{locationId}/areas/{areaId}/spaces/{spaceId}")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    @Sql("/sql/spaces/insert_one.sql")
    public void putSpaces() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .pathParam("spaceId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
            .body("""
                 {
                    "name": "Laboratório de Matemática",
                    "capacity": 20,
                    "type": "CLASSROOM"
                 }
            """)
            .log().all()
        .when()
            .put(spaceURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("Laboratório de Matemática"))
                .body("capacity", equalTo(20))
                .body("type",equalTo("CLASSROOM"))
                .body("area.id", notNullValue())
                .body("area.name", equalTo("Bloco A"))
                .body("area.reference", equalTo("Perto da entrada"))
                .body("area.location.id", notNullValue())
                .body("area.location.name", equalTo("IFSP Campus São Paulo"))
                .body("area.location.address", equalTo("Rua Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"));
    }

    @Test
    @DisplayName("PUT /locations/{locationId}/areas/{areaId}/spaces/{spaceId} - capacity only")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    @Sql("/sql/spaces/insert_one.sql")
    public void putSpacesChangeCapacityOnly() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .pathParam("spaceId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
            .body("""
                 {
                    "name": "Sala 308",
                    "capacity": 20,
                    "type": "LABORATORY"
                 }
            """)
            .log().all()
        .when()
            .put(spaceURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("Sala 308"))
                .body("capacity", equalTo(20))
                .body("type", equalTo("LABORATORY"))
                .body("area.id", notNullValue())
                .body("area.name", equalTo("Bloco A"))
                .body("area.reference", equalTo("Perto da entrada"))
                .body("area.location.id", notNullValue())
                .body("area.location.name", equalTo("IFSP Campus São Paulo"))
                .body("area.location.address", equalTo("Rua Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"));
    }

    @Test
    @DisplayName("PUT /locations/{locationId}/areas/{areaId}/spaces/{spaceId} - type only")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    @Sql("/sql/spaces/insert_one.sql")
    public void putSpacessChangeTypeOnly() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .pathParam("spaceId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
            .body("""
                 {
                    "name": "Sala 308",
                    "capacity": 15,
                    "type": "CLASSROOM"
                 }
            """)
            .log().all()
        .when()
            .put(spaceURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("Sala 308"))
                .body("capacity", equalTo(15))
                .body("type", equalTo("CLASSROOM"))
                .body("area.id", notNullValue())
                .body("area.name", equalTo("Bloco A"))
                .body("area.reference", equalTo("Perto da entrada"))
                .body("area.location.id", notNullValue())
                .body("area.location.name", equalTo("IFSP Campus São Paulo"))
                .body("area.location.address", equalTo("Rua Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"));
    }

    @Test
    @DisplayName("PUT /locations/{locationId}/areas/{areaId}/spaces/{spaceId} - capacity and type")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    @Sql("/sql/spaces/insert_one.sql")
    public void putSpacessChangeCapacityAndType() {
        given()
             .contentType(ContentType.JSON)
             .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
             .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
             .pathParam("spaceId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
             .body("""
                  {
                     "name": "Sala 308",
                     "capacity": 25,
                     "type": "CLASSROOM"
                  }
             """)
             .log().all()
        .when()
             .put(spaceURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("Sala 308"))
                .body("capacity", equalTo(25))
                .body("type", equalTo("CLASSROOM"))
                .body("area.id", notNullValue())
                .body("area.name", equalTo("Bloco A"))
                .body("area.reference", equalTo("Perto da entrada"))
                .body("area.location.id", notNullValue())
                .body("area.location.name", equalTo("IFSP Campus São Paulo"))
                .body("area.location.address", equalTo("Rua Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"));
    }


    @Test
    @DisplayName("PUT /locations/{locationId}/areas/{areaId}/spaces/{spaceId} - already exists")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    @Sql("/sql/spaces/insert_many.sql")
    public void putSpacesAlreadyExists() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .pathParam("spaceId", "28eaddf0-f12a-47e8-838d-48ecfe900a10")
            .body("""
                 {
                    "name": "Sala 308",
                    "capacity": 30,
                    "type": "CLASSROOM"
                 }
            """)
            .log().all()
        .when()
            .put(spaceURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Resource already exists exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PUT /locations/{locationId}/areas/{areaId}/spaces/{spaceId} - not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    @Sql("/sql/areas/insert_one.sql")
    @Sql("/sql/spaces/insert_one.sql")
    public void putSpacesNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .pathParam("spaceId", "a6118369-b18b-48e9-b172-ba23be91d9d5")
            .body("""
                 {
                    "name": "Laboratório de Matemática",
                    "capacity": 15,
                    "type": "LABORATORY"
                 }
            """)
            .log().all()
        .when()
            .put(spaceURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }
}

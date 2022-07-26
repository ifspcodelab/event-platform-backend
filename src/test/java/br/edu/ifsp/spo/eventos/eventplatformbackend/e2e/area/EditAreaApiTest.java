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
public class EditAreaApiTest {
    @LocalServerPort
    private int localPort;
    private String areasURI;
    private String areaURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        areasURI = "/api/v1/locations/{locationId}/areas";
        areaURI = "/api/v1/locations/{locationId}/areas/{areaId}";
    }

    @Test
    @DisplayName("PUT /locations/{locationId}/areas/{areaId}")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    @Sql("/sql/areas/insert_many.sql")
    public void putAreas() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .body("""
                {
                   "name": "Bloco A superior",
                   "reference": "Segundo andar"
                }
            """)
            .log().all()
        .when()
            .put(areaURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("Bloco A superior"))
                .body("reference", equalTo("Segundo andar"));
    }

    @Test
    @DisplayName("PUT /locations/{locationId}/areas/{areaId} - reference only")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    @Sql("/sql/areas/insert_many.sql")
    public void putAreasChangeReferenceOnly() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .body("""
                {
                   "name": "Bloco A",
                    "reference": "Piso superior"
                }
            """)
            .log().all()
        .when()
            .put(areaURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("Bloco A"))
                .body("reference", equalTo("Piso superior"));
    }


    @Test
    @DisplayName("PUT /locations/{locationId}/areas/{areaId} - already exists")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_many.sql")
    @Sql("/sql/areas/insert_many.sql")
    public void putAreasAlreadyExists() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "29eb3ccf-711d-40f2-954c-3f2616a6cf36")
            .body("""
                {
                   "name": "Bloco H",
                   "reference": "Perto da entrada"
                }
            """)
            .log().all()
        .when()
            .put(areaURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Resource already exists exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PUT /locations/{locationId}/areas/{areaId} - not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/locations/insert_one.sql")
    public void putAreasNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("locationId", "5607ddd3-31ed-4435-bd61-23133d2f3381")
            .pathParam("areaId", "19eb3ccf-711d-40f2-954c-3f2616a6cf31")
            .body("""
                {
                   "name": "Bloco H",
                   "reference": "Perto da Biblioteca"
                }
            """)
            .log().all()
        .when()
            .put(areaURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.e2e.event;

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
public class EventDetailsApiTest {
    @LocalServerPort
    private int localPort;
    private String eventURI;
    private String eventSubeventsURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        eventURI = "/api/v1/events/{eventId}";
        eventSubeventsURI = "/api/v1/events/{eventId}/sub-events";
    }

    @Test
    @DisplayName("GET /events/{eventId} - detalhes evento draft")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void getEvent() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .log().all()
        .when()
            .get(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("SEDCITEC"))
                .body("slug", equalTo("sedcitec"))
                .body("summary", equalTo("Semana de Educação, Ciência e Tecnologia"))
                .body("presentation", equalTo("Semana de Educação, Ciência e Tecnologia"))
                .body("registrationPeriod.startDate", equalTo("2022-07-28"))
                .body("registrationPeriod.endDate", equalTo("2022-08-28"))
                .body("executionPeriod.startDate", equalTo("2022-09-05"))
                .body("executionPeriod.endDate", equalTo("2022-09-09"))
                .body("status", equalTo("DRAFT"));
    }

    @Test
    @DisplayName("GET /events/{eventId} - not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void getEventNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
            .log().all()
        .when()
            .get(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("GET /events/{evenId}/sub-events - empty list")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void getAllSubeventsEmptyList() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "8ae2bd06-7358-4700-a20e-af4da8ae6c36")
            .log().all()
        .when()
            .get(eventSubeventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("GET /events/{evenId}/sub-events - one sub-event")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_one.sql")
    @Sql("/sql/sub-events/insert_one.sql")
    public void getAllSubeventsOne() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .log().all()
        .when()
            .get(eventSubeventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(1));
    }

    @Test
    @DisplayName("GET /events/{evenId}/sub-events - many sub-events")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_published.sql")
    @Sql("/sql/sub-events/insert_many.sql")
    public void getAllSubeventsMany() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
                .log().all()
                .when()
                .get(eventSubeventsURI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("$", hasSize(3));
    }
}

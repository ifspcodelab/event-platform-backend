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
public class PublishEventApiTest {
    @LocalServerPort
    private int localPort;
    private String eventURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        eventURI = "/api/v1/events/{eventId}/publish";

    }

    @Test
    @DisplayName("PATCH /events/{eventId}/publish - publish a draft")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_draft.sql")
    public void patchEvent() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .log().all()
        .when()
            .patch(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("SEDCITEC"))
                .body("slug", equalTo("sedcitec"))
                .body("summary", equalTo("Semana de Educação, Ciência e Tecnologia"))
                .body("presentation", equalTo("Semana de Educação, Ciência e Tecnologia"))
                .body("registrationPeriod.startDate", equalTo("2022-09-01"))
                .body("registrationPeriod.endDate", equalTo("2022-09-28"))
                .body("executionPeriod.startDate", equalTo("2022-11-05"))
                .body("executionPeriod.endDate", equalTo("2022-11-09"))
                .body("status", equalTo("PUBLISHED"));
    }

    @Test
    @DisplayName("PATCH /events/{eventId}/publish - draft registration started")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_draft.sql")
    public void patchEventRegistrationStartBeforeToday() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "13c95893-1c49-46e5-bfba-29923e035d67")
            .log().all()
        .when()
            .patch(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PATCH /events/{eventId}/publish - draft registration finished")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_draft.sql")
    public void patchEventRegistrationPeriodBeforeToday() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "0d4dcb8d-6384-4ec4-9e45-15069b20772a")
            .log().all()
        .when()
            .patch(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PATCH /events/{eventId}/publish - draft execution started")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_draft.sql")
    public void patchEventExecutionStarted() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "f3c829db-0334-4fb3-aa65-ea8b09345f98")
            .log().all()
        .when()
            .patch(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PATCH /events/{eventId}/publish - already published")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void patchEventPublished() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "8ae2bd06-7358-4700-a20e-af4da8ae6c36")
            .log().all()
        .when()
            .patch(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PATCH /events/{eventId}/publish - already canceled")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void patchEventCanceled() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "05110533-ea4a-4db5-8fa6-fd9de2b7be7f")
            .log().all()
        .when()
            .patch(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

}

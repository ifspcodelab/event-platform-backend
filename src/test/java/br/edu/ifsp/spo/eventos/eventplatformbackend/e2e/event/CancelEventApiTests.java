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
public class CancelEventApiTests {
    @LocalServerPort
    private int localPort;
    private String eventURI;
    private String eventSubeventsURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        eventURI = "/api/v1/events/{eventId}/cancel";
        eventSubeventsURI = "/api/v1/events/{eventId}/sub-events";

    }

    @Test
    @DisplayName("PATCH /events/{eventId}/cancel - published registration started")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_published.sql")
    public void patchEventCancelRegistrationStarted() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "13c95893-1c49-46e5-bfba-29923e035d67")
            .log().all()
        .when()
            .patch(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("SEDCITEC 2"))
                .body("slug", equalTo("sedcitec-2"))
                .body("summary", equalTo("Semana de Educação, Ciência e Tecnologia"))
                .body("presentation", equalTo("Semana de Educação, Ciência e Tecnologia"))
                .body("registrationPeriod.startDate", equalTo("2022-08-01"))
                .body("registrationPeriod.endDate", equalTo("2022-09-28"))
                .body("executionPeriod.startDate", equalTo("2022-11-05"))
                .body("executionPeriod.endDate", equalTo("2022-11-09"))
                .body("status", equalTo("CANCELED"));
    }

    @Test
    @DisplayName("PATCH /events/{eventId}/cancel - published registration ended yet to start execution")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_published.sql")
    public void patchEventCancelResgistrationEndedExecutionWillStart() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "0d4dcb8d-6384-4ec4-9e45-15069b20772a")
            .log().all()
        .when()
            .patch(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("SEDCITEC 3"))
                .body("slug", equalTo("sedcitec-3"))
                .body("summary", equalTo("Semana de Educação, Ciência e Tecnologia"))
                .body("presentation", equalTo("Semana de Educação, Ciência e Tecnologia"))
                .body("registrationPeriod.startDate", equalTo("2022-07-01"))
                .body("registrationPeriod.endDate", equalTo("2022-07-28"))
                .body("executionPeriod.startDate", equalTo("2022-11-05"))
                .body("executionPeriod.endDate", equalTo("2022-11-09"))
                .body("status", equalTo("CANCELED"));
    }

    @Test
    @DisplayName("PATCH /events/{eventId}/cancel - published execution started")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_published.sql")
    @Sql("/sql/sub-events/insert_many.sql")
    public void patchEventCancelExecutionStartedAlsoSubevent() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "f3c829db-0334-4fb3-aa65-ea8b09345f98")
            .log().all()
        .when()
            .patch(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("SEDCITEC 4"))
                .body("slug", equalTo("sedcitec-4"))
                .body("summary", equalTo("Semana de Educação, Ciência e Tecnologia"))
                .body("presentation", equalTo("Semana de Educação, Ciência e Tecnologia"))
                .body("registrationPeriod.startDate", equalTo("2022-07-01"))
                .body("registrationPeriod.endDate", equalTo("2022-07-28"))
                .body("executionPeriod.startDate", equalTo("2022-07-30"))
                .body("executionPeriod.endDate", equalTo("2022-11-09"))
                .body("status", equalTo("CANCELED"));
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "f3c829db-0334-4fb3-aa65-ea8b09345f98")
            .log().all()
        .when()
            .get(eventSubeventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id",notNullValue())
                .body("[0].status",equalTo("CANCELED"));
    }

    @Test
    @DisplayName("PATCH /events/{eventId}/cancel - published already finished")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_published.sql")
    public void patchEventCancelFinishedEvent() {
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
    @DisplayName("PATCH /events/{eventId}/cancel - registration did not start")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_published.sql")
    public void patchEventCancelYetToStartRegistration() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
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
    @DisplayName("PATCH /events/{eventId}/cancel - draft")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_draft.sql")
    public void patchEventCancelDraft() {
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
    @DisplayName("PATCH /events/{eventId}/cancel - already canceled")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void patchEventCancelEventAlreadyCanceled() {
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

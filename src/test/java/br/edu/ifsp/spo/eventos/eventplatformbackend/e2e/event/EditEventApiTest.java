package br.edu.ifsp.spo.eventos.eventplatformbackend.e2e.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.e2e.testjsonbodies.events.TestEventBodies;
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
public class EditEventApiTest {
    @LocalServerPort
    private int localPort;
    private String eventURI;
    private final TestEventBodies eventBody = new TestEventBodies();

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        eventURI = "/api/v1/events/{eventId}";
    }

    @Test
    @DisplayName("PUT /events/{eventId}")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void putEvents() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .body(eventBody.getValidCompleteEditedEvent())
            .log().all()
        .when()
            .put(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo(eventBody.getValidTitle()))
                .body("slug", equalTo(eventBody.getValidSlug()))
                .body("summary", notNullValue())
                .body("summary", isA(String.class))
                .body("presentation", notNullValue())
                .body("presentation", isA(String.class))
                .body("registrationPeriod.startDate", equalTo(eventBody.getValidRegistrationStartDate()))
                .body("registrationPeriod.endDate", equalTo(eventBody.getValidRegistrationEndDate()))
                .body("executionPeriod.startDate", equalTo(eventBody.getValidEventStartDate()))
                .body("executionPeriod.endDate", equalTo(eventBody.getValidEventEndDate()))
                .body("status", equalTo("DRAFT"));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - draft - title already exists")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void putEventsAlreadyExists() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .body(eventBody.getEditedDraftEventWithRepetedTitle())
        .when()
            .put(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Resource already exists exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - published - title already exists")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void putEventsPublishedAlreadyExists() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "8ae2bd06-7358-4700-a20e-af4da8ae6c36")
            .body(eventBody.getEditedPublishedEventWithRepetedTitle())
        .when()
            .put(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Resource already exists exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_one.sql")
    public void putEventsNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "05110533-ea4a-4db5-8fa6-fd9de2b7be7f")
            .body(eventBody.getValidCompleteEditedEvent())
            .log().all()
        .when()
            .put(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - canceled event")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void putEventsEditeCanceledEvent() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "05110533-ea4a-4db5-8fa6-fd9de2b7be7f")
            .body(eventBody.getValidCompleteEditedEvent())
            .log().all()
        .when()
            .put(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - published event already ended")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void putEventsEditeStartedPublichedEvent() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "8ae2bd06-7358-4700-a20e-af4da8ae6c36")
            .body(eventBody.getValidCompleteEditedEvent())
            .log().all()
        .when()
            .put(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - registration start after event start")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_one.sql")
    public void putEventsInvalidRegistrationStartDate() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .body(eventBody.getEventBodyWithRegistrationStartDateAfterEventStartDate())
            .log().all()
        .when()
            .put(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - registration end after event end")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_one.sql")
    public void putEventsInvalidRegistrationEndDate() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .body(eventBody.getEventBodyWithRegistrationEndDateAfterEventEndDate())
            .log().all()
        .when()
            .put(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - registration start before today")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_one.sql")
    public void putEventsRegistrationStartDateBeforeToday() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .body(eventBody.getEventBodyWithRegistrationDateBeforeToday())
            .log().all()
        .when()
            .put(eventURI)
        .then()
            .log().all()
                .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - execution start before today")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_one.sql")
    public void putEventsExecutionStartDateBeforeToday() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .body(eventBody.getEventBodyWithExecutionDateBeforeToday())
            .log().all()
        .when()
            .put(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - edit summary and presentation")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_published.sql")
    public void putEventsEditSummaryAndPresentation() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .body(eventBody.getValidSummayAndPresentationEditedEvent("SEDCITEC", "sedcitec","2022-09-01", "2022-09-28", "2022-11-05", "2022-11-09"))
            .log().all()
        .when()
            .put(eventURI)
        .then()
            .log().all()
            .assertThat()
            .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("SEDCITEC"))
                .body("slug", equalTo("sedcitec"))
                .body("summary", notNullValue())
                .body("summary", isA(String.class))
                .body("presentation", notNullValue())
                .body("presentation", isA(String.class))
                .body("registrationPeriod.startDate", equalTo("2022-09-01"))
                .body("registrationPeriod.endDate", equalTo("2022-09-28"))
                .body("executionPeriod.startDate", equalTo("2022-11-05"))
                .body("executionPeriod.endDate", equalTo("2022-11-09"))
                .body("status", equalTo("PUBLISHED"));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - edit summary and presentation - open registration")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_published.sql")
    public void putEventsEditSummaryAndPresentationOpenRegistration() {
        given()
        .contentType(ContentType.JSON)
        .pathParam("eventId", "13c95893-1c49-46e5-bfba-29923e035d67")
        .body(eventBody.getValidSummayAndPresentationEditedEvent("SEDCITEC 2", "sedcitec-2", "2022-08-01", "2022-09-28", "2022-11-05", "2022-11-09"))
        .log().all()
        .when()
        .put(eventURI)
        .then()
        .log().all()
        .assertThat()
        .statusCode(200)
        .body("id", notNullValue())
        .body("title", equalTo("SEDCITEC 2"))
        .body("slug", equalTo("sedcitec-2"))
        .body("summary", notNullValue())
        .body("summary", isA(String.class))
        .body("presentation", notNullValue())
        .body("presentation", isA(String.class))
        .body("registrationPeriod.startDate", equalTo("2022-08-01"))
        .body("registrationPeriod.endDate", equalTo("2022-09-28"))
        .body("executionPeriod.startDate", equalTo("2022-11-05"))
        .body("executionPeriod.endDate", equalTo("2022-11-09"))
        .body("status", equalTo("PUBLISHED"));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - edit summary and presentation - closed registration")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_published.sql")
    public void putEventsEditSummaryAndPresentationClosedRegistration() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("eventId", "0d4dcb8d-6384-4ec4-9e45-15069b20772a")
                .body(eventBody.getValidSummayAndPresentationEditedEvent("SEDCITEC 3", "sedcitec-3","2022-07-01", "2022-07-28", "2022-11-05", "2022-11-09"))
                .log().all()
                .when()
                .put(eventURI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("SEDCITEC 3"))
                .body("slug", equalTo("sedcitec-3"))
                .body("summary", notNullValue())
                .body("summary", isA(String.class))
                .body("presentation", notNullValue())
                .body("presentation", isA(String.class))
                .body("registrationPeriod.startDate", equalTo("2022-07-01"))
                .body("registrationPeriod.endDate", equalTo("2022-07-28"))
                .body("executionPeriod.startDate", equalTo("2022-11-05"))
                .body("executionPeriod.endDate", equalTo("2022-11-09"))
                .body("status", equalTo("PUBLISHED"));
    }

    @Test
    @DisplayName("PUT /events/{eventId} - edit summary and presentation - open execution")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_published.sql")
    public void putEventsEditSummaryAndPresentationOnExecution() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "f3c829db-0334-4fb3-aa65-ea8b09345f98")
            .body(eventBody.getValidSummayAndPresentationEditedEvent("SEDCITEC 4", "sedcitec-4", "2022-07-01", "2022-07-28", "2022-07-30", "2022-11-09"))
            .log().all()
        .when()
            .put(eventURI)
        .then()
            .log().all()
            .assertThat()
            .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("SEDCITEC 4"))
                .body("slug", equalTo("sedcitec-4"))
                .body("summary", notNullValue())
                .body("summary", isA(String.class))
                .body("presentation", notNullValue())
                .body("presentation", isA(String.class))
                .body("registrationPeriod.startDate", equalTo("2022-07-01"))
                .body("registrationPeriod.endDate", equalTo("2022-07-28"))
                .body("executionPeriod.startDate", equalTo("2022-07-30"))
                .body("executionPeriod.endDate", equalTo("2022-11-09"))
                .body("status", equalTo("PUBLISHED"));
    }
}

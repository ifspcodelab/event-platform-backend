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
            .body(eventBody.getValidEditedEvent())
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
    @DisplayName("PUT /events/{eventId} - already exists")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void putEventsAlreadyExists() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .body(eventBody.getEditedEventWithRepetedTitle())
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
            .body(eventBody.getValidEditedEvent())
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
            .body(eventBody.getValidEditedEvent())
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
}

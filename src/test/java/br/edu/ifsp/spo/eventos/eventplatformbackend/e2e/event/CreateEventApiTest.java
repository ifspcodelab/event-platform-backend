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
public class CreateEventApiTest {
    @LocalServerPort
    private int localPort;
    private String eventsURI;
    private final TestEventBodies eventBody = new TestEventBodies();

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        eventsURI = "/api/v1/events";
    }

    @Test
    @DisplayName("POST /events")
    @Sql("/sql/delete_all_tables.sql")
    public void postEvents() {
        given()
            .contentType(ContentType.JSON)
            .body(eventBody.getValidEventBody())
            .log().all()
        .when()
            .post(eventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("SEDCITEC"))
                .body("slug", equalTo("sedcitec"))
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
    @DisplayName("POST /events - already exists")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_one.sql")
    public void postEventsAlreadyExists() {
        given()
            .contentType(ContentType.JSON)
            .body(eventBody.getValidEventBody())
            .log().all()
        .when()
            .post(eventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Resource already exists exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("POST /events - registration start after event start")
    @Sql("/sql/delete_all_tables.sql")
    public void postEventsInvalidRegistrationStartDate() {
        given()
            .contentType(ContentType.JSON)
            .body(eventBody.getEventBodyWithRegistrationStartDateAfterEventStartDate())
            .log().all()
        .when()
            .post(eventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("POST /events - registration end after event end")
    @Sql("/sql/delete_all_tables.sql")
    public void postEventsInvalidRegistrationEndDate() {
        given()
            .contentType(ContentType.JSON)
            .body(eventBody.getEventBodyWithRegistrationEndDateAfterEventEndDate())
            .log().all()
        .when()
            .post(eventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("POST /events - registration start before today")
    @Sql("/sql/delete_all_tables.sql")
    public void postEventsRegistrationStartDateBeforeToday() {
        given()
            .contentType(ContentType.JSON)
            .body(eventBody.getEventBodyWithRegistrationDateBeforeToday())
            .log().all()
        .when()
            .post(eventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("POST /events - execution start before today")
    @Sql("/sql/delete_all_tables.sql")
    public void postEventsExecutionStartDateBeforeToday() {
        given()
            .contentType(ContentType.JSON)
            .body(eventBody.getEventBodyWithExecutionDateBeforeToday())
            .log().all()
        .when()
            .post(eventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }
}

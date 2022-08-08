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
public class DeleteEventApiTests {
    @LocalServerPort
    private int localPort;
    private String eventURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        eventURI = "/api/v1/events/{eventId}";
    }

    @Test
    @DisplayName("DELETE /events/{eventId} - draft event")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void deleteEvent() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .log().all()
        .when()
            .delete(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(204);
    }

    @Test
    @DisplayName("DELETE /events/{eventId} - published status")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many_published.sql")
    public void deleteEventWithPublishedStatusFutureDates() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
                .log().all()
                .when()
                .delete(eventURI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(204);
    }

    @Test
    @DisplayName("DELETE /events/{eventId} - not found")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void deleteEventNotFound() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "a7118369-b18b-48e9-b172-ba23be91d9d5")
            .log().all()
        .when()
            .delete(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(404)
                .body("title", equalTo("Resource not found exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("DELETE /events/{eventId} - with subevents associated")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_one.sql")
    @Sql("/sql/sub-events/insert_one.sql")
    public void deleteEventWithSubeventsAssociated() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "b11fd168-eeaa-410e-b182-ab3625e13368")
            .log().all()
        .when()
            .delete(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("DELETE /events/{eventId} - canceled status")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void deleteEventWithCanceledStatus() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "05110533-ea4a-4db5-8fa6-fd9de2b7be7f")
            .log().all()
        .when()
            .delete(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

    @Test
    @DisplayName("DELETE /events/{eventId} - published status finished")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void deleteEventWithPublishedStatusFinished() {
        given()
            .contentType(ContentType.JSON)
            .pathParam("eventId", "8ae2bd06-7358-4700-a20e-af4da8ae6c36")
            .log().all()
        .when()
            .delete(eventURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(409)
                .body("title", equalTo("Business rule exception"))
                .body("violations", hasSize(1));
    }

}

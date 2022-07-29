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
public class ListEventApiTest {
    @LocalServerPort
    private int localPort;
    private String eventsURI;

    @BeforeEach
    public void beforeEach() {
        baseURI = "http://localhost";
        port = localPort;
        eventsURI = "/api/v1/events";
    }

    @Test
    @DisplayName("GET /events - empty list")
    @Sql("/sql/delete_all_tables.sql")
    public void getAllEventsEmptyList() {
        given()
            .contentType(ContentType.JSON)
            .log().all()
        .when()
            .get(eventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("GET /events - one location inside list")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_one.sql")
    public void getListOneEvent() {
        given()
            .contentType(ContentType.JSON)
            .log().all()
        .when()
            .get(eventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(1));
    }

    @Test
    @DisplayName("GET /events - multiple events inside list")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_many.sql")
    public void getListMultipleEvents() {
        given()
            .contentType(ContentType.JSON)
            .log().all()
        .when()
            .get(eventsURI)
        .then()
            .log().all()
            .assertThat()
                .statusCode(200)
                .body("$", hasSize(3));
    }
}

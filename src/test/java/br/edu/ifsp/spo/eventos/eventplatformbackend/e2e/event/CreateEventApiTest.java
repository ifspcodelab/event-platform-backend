package br.edu.ifsp.spo.eventos.eventplatformbackend.e2e.event;

import io.restassured.http.ContentType;
import org.json.JSONObject;
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
            .body("""
                {
                    "title": "SEDCITEC",
                    "slug": "sedcitec",
                    "summary": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas mollis ullamcorper hendrerit. In aliquet dolor id felis dignissim ornare.",
                    "presentation": "Semana de Educação, Ciência e Tecnologia. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas mollis ullamcorper hendrerit. In aliquet dolor id felis dignissim ornare. Proin placerat sapien id felis vehicula porta. Nunc lobortis libero sagittis convallis posuere. Integer nec faucibus lorem. Praesent elementum lobortis leo ac mollis. Vivamus euismod est eu dui ullamcorper eleifend. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Ut in quam nisl. Nulla lectus lacus, pretium vitae purus vel, lacinia imperdiet metus. Cras mattis vulputate aliquam. Integer suscipit nisi id lorem placerat, in ornare dui eleifend. Phasellus ut nisl eu purus rhoncus mattis sed non purus. Integer dapibus ornare est, at interdum eros. Morbi eget nibh accumsan ligula dictum ultricies non quis libero. Sed imperdiet lorem dapibus nunc egestas, et pellentesque magna tincidunt. Aenean velit orci, sodales et ultricies vel, cursus nec velit. Ut ullamcorper mi nulla, nec scelerisque lacus aliquam consectetur. Fusce nec sapien purus. Aliquam suscipit tincidunt nisi sed tincidunt. Nullam luctus est eu lobortis lobortis. Mauris nec blandit purus. Etiam interdum ullamcorper mattis.",
                    "registrationPeriod": {
                        "startDate": "2022-07-28",
                        "endDate": "2022-08-28"
                    },
                    "executionPeriod": {
                        "startDate": "2022-09-05",
                        "endDate": "2022-09-09"
                    },
                    "smallerImage": null,
                    "biggerImage": null
                }
            """)
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
                .body("registrationPeriod.startDate", equalTo("2022-07-28"))
                .body("registrationPeriod.endDate", equalTo("2022-08-28"))
                .body("executionPeriod.startDate", equalTo("2022-09-05"))
                .body("executionPeriod.endDate", equalTo("2022-09-09"))
                .body("status", equalTo("DRAFT"));
    }

    @Test
    @DisplayName("POST /events - already exists")
    @Sql("/sql/delete_all_tables.sql")
    @Sql("/sql/events/insert_one.sql")
    public void postEventsAlreadyExists() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "title": "SEDCITEC",
                    "slug": "sedcitec",
                    "summary": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas mollis ullamcorper hendrerit. In aliquet dolor id felis dignissim ornare.",
                    "presentation": "Semana de Educação, Ciência e Tecnologia. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas mollis ullamcorper hendrerit. In aliquet dolor id felis dignissim ornare. Proin placerat sapien id felis vehicula porta. Nunc lobortis libero sagittis convallis posuere. Integer nec faucibus lorem. Praesent elementum lobortis leo ac mollis. Vivamus euismod est eu dui ullamcorper eleifend. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Ut in quam nisl. Nulla lectus lacus, pretium vitae purus vel, lacinia imperdiet metus. Cras mattis vulputate aliquam. Integer suscipit nisi id lorem placerat, in ornare dui eleifend. Phasellus ut nisl eu purus rhoncus mattis sed non purus. Integer dapibus ornare est, at interdum eros. Morbi eget nibh accumsan ligula dictum ultricies non quis libero. Sed imperdiet lorem dapibus nunc egestas, et pellentesque magna tincidunt. Aenean velit orci, sodales et ultricies vel, cursus nec velit. Ut ullamcorper mi nulla, nec scelerisque lacus aliquam consectetur. Fusce nec sapien purus. Aliquam suscipit tincidunt nisi sed tincidunt. Nullam luctus est eu lobortis lobortis. Mauris nec blandit purus. Etiam interdum ullamcorper mattis.",
                    "registrationPeriod": {
                        "startDate": "2022-08-01",
                        "endDate": "2022-08-08"
                    },
                    "executionPeriod": {
                        "startDate": "2022-08-22",
                        "endDate": "2022-08-27"
                    },
                    "smallerImage": null,
                    "biggerImage": null
                }
            """)
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
}

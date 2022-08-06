package br.edu.ifsp.spo.eventos.eventplatformbackend.e2e.testjsonbodies.events;

import java.time.LocalDate;

public class TestEventBodies {

    public String getValidRegistrationStartDate(){
        return LocalDate.now().toString();
    }

    public String getValidRegistrationEndDate(){
        return LocalDate.now().plusWeeks(1).toString();
    }

    public String getValidEventStartDate(){
        return LocalDate.now().plusWeeks(1).plusDays(1).toString();
    }

    public String getValidEventEndDate(){
        return LocalDate.now().plusWeeks(2).plusDays(1).toString();
    }

    public String getRegistrationStartAfterEventStart(){
        return LocalDate.now().plusWeeks(1).plusDays(2).toString();
    }

    public String getRegistrationEndAfterEventStart(){
        return LocalDate.now().plusWeeks(1).plusDays(3).toString();
    }

    public String getRegistrationEndAfterEventEnd(){
        return LocalDate.now().plusWeeks(2).plusDays(2).toString();
    }

    public String getRegistrationStartDateBeforeToday(){
        return LocalDate.now().minusDays(3).toString();
    }

    public String getEventStartDateBeforeToday(){
        return LocalDate.now().minusDays(1).toString();
    }

    public String getValidTitle(){
        return "Evento de ciência";
    }

    public String getValidSlug(){
        return "evento-de-ciencia";
    }

    public String getValidSummary(){
        return ("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas mollis ullamcorper hendrerit. In aliquet dolor id felis dignissim ornare.");
    }

    public String getValidPresentation(){
        return ("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas mollis ullamcorper hendrerit. In aliquet dolor id felis dignissim ornare. Proin placerat sapien id felis vehicula porta. Nunc lobortis libero sagittis convallis posuere. Integer nec faucibus lorem. Praesent elementum lobortis leo ac mollis. Vivamus euismod est eu dui ullamcorper eleifend. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Ut in quam nisl. Nulla lectus lacus, pretium vitae purus vel, lacinia imperdiet metus. Cras mattis vulputate aliquam. Integer suscipit nisi id lorem placerat, in ornare dui eleifend. Phasellus ut nisl eu purus rhoncus mattis sed non purus. Integer dapibus ornare est, at interdum eros. Morbi eget nibh accumsan ligula dictum ultricies non quis libero. Sed imperdiet lorem dapibus nunc egestas, et pellentesque magna tincidunt. Aenean velit orci, sodales et ultricies vel, cursus nec velit. Ut ullamcorper mi nulla, nec scelerisque lacus aliquam consectetur. Fusce nec sapien purus. Aliquam suscipit tincidunt nisi sed tincidunt. Nullam luctus est eu lobortis lobortis. Mauris nec blandit purus. Etiam interdum ullamcorper mattis.");
    }

    public String getValidEventBody(){
        return getEventBodyToChangeDates(
                getValidRegistrationStartDate(),
                getValidRegistrationEndDate(),
                getValidEventStartDate(),
                getValidEventEndDate()
        );
    }

    public String getEventBodyWithRegistrationStartDateAfterEventStartDate(){
        return getEventBodyToChangeDates(
                getRegistrationStartAfterEventStart(),
                getRegistrationEndAfterEventStart(),
                getValidEventStartDate(),
                getValidEventEndDate()
        );
    }

    public String getEventBodyWithRegistrationEndDateAfterEventEndDate(){
        return getEventBodyToChangeDates(
                getValidRegistrationStartDate(),
                getRegistrationEndAfterEventEnd(),
                getValidEventStartDate(),
                getValidEventEndDate()
        );
    }

    public String getEventBodyWithRegistrationDateBeforeToday(){
        return getEventBodyToChangeDates(
                getRegistrationStartDateBeforeToday(),
                getValidRegistrationEndDate(),
                getValidEventStartDate(),
                getValidEventEndDate()
        );
    }

    public String getEventBodyWithExecutionDateBeforeToday(){
        return getEventBodyToChangeDates(
                getValidRegistrationStartDate(),
                getValidRegistrationEndDate(),
                getEventStartDateBeforeToday(),
                getValidEventEndDate()
        );
    }

    public String getValidCompleteEditedEvent(){
        return getEventBodyToEdit(
                getValidTitle(),
                getValidSlug(),
                getValidSummary(),
                getValidPresentation(),
                getValidRegistrationStartDate(),
                getValidRegistrationEndDate(),
                getValidEventStartDate(),
                getValidEventEndDate()
        );
    }

    public String getValidSummayAndPresentationEditedEvent(String title, String slug, String regStartDate, String regEndDate, String execStartDAte, String execEndDate){
        return getEventBodyToEdit(
                title,
                slug,
                getValidSummary(),
                getValidPresentation(),
                regStartDate,
                regEndDate,
                execStartDAte,
                execEndDate
        );
    }

    public String getEditedDraftEventWithRepetedTitle(){
        return getEventBodyToEdit(
                "FEIRA DE PROFISSÕES",
                "sedcitec",
                getValidSummary(),
                getValidPresentation(),
                getValidRegistrationStartDate(),
                getValidRegistrationEndDate(),
                getValidEventStartDate(),
                getValidEventEndDate()
        );
    }

    public String getEditedPublishedEventWithRepetedTitle(){
        return getEventBodyToEdit(
                "SEDCITEC",
                "feira-de-profissoes",
                getValidSummary(),
                getValidPresentation(),
                getValidRegistrationStartDate(),
                getValidRegistrationEndDate(),
                getValidEventStartDate(),
                getValidEventEndDate()
        );
    }

    private String getEventBodyToChangeDates(String regStartDate, String regEndDate, String execStartDate, String execEndDate){
        return ("""
                {
                    "title": "SEDCITEC",
                    "slug": "sedcitec",
                    "summary": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas mollis ullamcorper hendrerit. In aliquet dolor id felis dignissim ornare.",
                    "presentation": "Semana de Educação, Ciência e Tecnologia. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas mollis ullamcorper hendrerit. In aliquet dolor id felis dignissim ornare. Proin placerat sapien id felis vehicula porta. Nunc lobortis libero sagittis convallis posuere. Integer nec faucibus lorem. Praesent elementum lobortis leo ac mollis. Vivamus euismod est eu dui ullamcorper eleifend. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Ut in quam nisl. Nulla lectus lacus, pretium vitae purus vel, lacinia imperdiet metus. Cras mattis vulputate aliquam. Integer suscipit nisi id lorem placerat, in ornare dui eleifend. Phasellus ut nisl eu purus rhoncus mattis sed non purus. Integer dapibus ornare est, at interdum eros. Morbi eget nibh accumsan ligula dictum ultricies non quis libero. Sed imperdiet lorem dapibus nunc egestas, et pellentesque magna tincidunt. Aenean velit orci, sodales et ultricies vel, cursus nec velit. Ut ullamcorper mi nulla, nec scelerisque lacus aliquam consectetur. Fusce nec sapien purus. Aliquam suscipit tincidunt nisi sed tincidunt. Nullam luctus est eu lobortis lobortis. Mauris nec blandit purus. Etiam interdum ullamcorper mattis.",""" +
                "\"registrationPeriod\": {\n" +
                "\"startDate\":\"" + regStartDate + "\",\n" +
                "\"endDate\": \"" + regEndDate + "\"\n" +
                "},\n" +
                "\"executionPeriod\": { \n" +
                "\"startDate\": \"" + execStartDate + "\",\n"+
                "\"endDate\": \"" + execEndDate + "\"\n" +
                "},\n"+
                "\"smallerImage\": null,\n"+
                "\"biggerImage\": null\n"+
                "}");
    }

    private String getEventBodyToEdit(String title, String slug, String summary, String presentation, String regStartDate, String regEndDate, String execStartDate, String execEndDate){
        return ("{\n" +
                    "\"title\": \"" + title + "\",\n"+
                    "\"slug\": \"" + slug + "\",\n" +
                    "\"summary\": \"" + summary + "\",\n" +
                    "\"presentation\": \"" + presentation + "\",\n" +
                    "\"registrationPeriod\": {\n" +
                        "\"startDate\":\"" + regStartDate + "\",\n" +
                        "\"endDate\": \"" + regEndDate + "\"\n" +
                    "},\n" +
                    "\"executionPeriod\": { \n" +
                        "\"startDate\": \"" + execStartDate + "\",\n"+
                        "\"endDate\": \"" + execEndDate + "\"\n" +
                    "},\n"+
                    "\"smallerImage\": null,\n"+
                    "\"biggerImage\": null\n"+
                "}");
    }
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

public class SessionScheduleTest {

    public SessionSchedule createSessionSchedule(LocalDateTime start, LocalDateTime end) {
        return new SessionSchedule(start, end, "", null, null, null);
    }

    // s1  s------e
    // s2  s------e
    @Test
    public void sameTime() {
        SessionSchedule s1 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 8, 0, 0), LocalDateTime.of(2022, 1, 1, 9, 0, 0)
        );
        SessionSchedule s2 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 8, 0, 0), LocalDateTime.of(2022, 1, 1, 9, 0, 0)
        );
        assertThat(s1.hasIntersection(s2)).isTrue();
    }

    // s1  s------e
    // s2      s------e
    // ------------------
    // s2      s------e
    // s1  s------e
    @Test
    public void partialIntersection() {
        SessionSchedule s1 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 8, 0, 0), LocalDateTime.of(2022, 1, 1, 9, 0, 0)
        );
        SessionSchedule s2 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 8, 45, 0), LocalDateTime.of(2022, 1, 1, 9, 45, 0)
        );
        assertThat(s1.hasIntersection(s2)).isTrue();
        assertThat(s2.hasIntersection(s1)).isTrue();
    }

    // s1  s------e
    // s2    s--e
    // ------------------
    // s2    s--e
    // s1  s------e
    @Test
    public void totalIntersection() {
        SessionSchedule s1 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 8, 0, 0), LocalDateTime.of(2022, 1, 1, 9, 0, 0)
        );
        SessionSchedule s2 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 8, 15, 0), LocalDateTime.of(2022, 1, 1, 8, 45, 0)
        );
        assertThat(s1.hasIntersection(s2)).isTrue();
        assertThat(s2.hasIntersection(s1)).isTrue();
    }

    // s1  s------e
    // s2                 s------e
    //    ------------------------------------
    // s2                 s------e
    // s1  s------e
    @Test
    public void sameTimeDifferentDays() {
        SessionSchedule s1 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 8, 0, 0), LocalDateTime.of(2022, 1, 1, 9, 0, 0)
        );
        SessionSchedule s2 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 2, 8, 0, 0), LocalDateTime.of(2022, 1, 2, 9, 0, 0)
        );
        assertThat(s1.hasIntersection(s2)).isFalse();
        assertThat(s2.hasIntersection(s1)).isFalse();
    }

    // s1  s------e
    // s2          s------e
    //    ------------------------------------
    // s2          s------e
    // s1  s------e
    @Test
    public void sameDayDifferentSecond() {
        SessionSchedule s1 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 8, 0, 0), LocalDateTime.of(2022, 1, 1, 9, 0, 0)
        );
        SessionSchedule s2 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 9, 0, 1), LocalDateTime.of(2022, 1, 1, 10, 0, 0)
        );
        assertThat(s1.hasIntersection(s2)).isFalse();
        assertThat(s2.hasIntersection(s1)).isFalse();
    }

//     s1  s------e
//     s2         s------e
//        ------------------------------------
//     s2         s------e
//     s1  s------e
    @Test
    public void sameDaySameSecond() {
        SessionSchedule s1 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 8, 0, 0), LocalDateTime.of(2022, 1, 1, 9, 0, 0)
        );
        SessionSchedule s2 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 9, 0, 0), LocalDateTime.of(2022, 1, 1, 10, 0, 0)
        );
        assertThat(s1.hasIntersection(s2)).isFalse();
        assertThat(s2.hasIntersection(s1)).isFalse();
    }

    //     s1  s------e
    //     s2         s------e
    //        ------------------------------------
    //     s2         s------e
    //     s1  s------e
    @Test
    public void sameDayTwoSecondIntersection() {
        SessionSchedule s1 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 8, 0, 0), LocalDateTime.of(2022, 1, 1, 9, 0, 0)
        );
        SessionSchedule s2 = createSessionSchedule(
            LocalDateTime.of(2022, 1, 1, 8, 59, 0), LocalDateTime.of(2022, 1, 1, 10, 0, 0)
        );
        assertThat(s1.hasIntersection(s2)).isTrue();
        assertThat(s2.hasIntersection(s1)).isTrue();
    }
}

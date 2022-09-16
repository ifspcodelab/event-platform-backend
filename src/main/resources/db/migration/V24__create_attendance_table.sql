CREATE TABLE attendances(
    id UUID NOT NULL,
    registration_id UUID NOT NULL,
    created_at TIMESTAMP,
    session_schedule_id UUID NOT NULL,
    CONSTRAINT attendance_pk PRIMARY KEY (id),
    CONSTRAINT registrations_fk FOREIGN KEY (registration_id) REFERENCES registrations(id),
    CONSTRAINT session_schedule_fk FOREIGN KEY (session_schedule_id) REFERENCES sessions_schedules(id)
)
CREATE TABLE activities_speakers(
   id UUID NOT NULL,
   activity_id UUID NOT NULL,
   speaker_id UUID,
   CONSTRAINT activities_speakers_pk PRIMARY KEY (id),
   CONSTRAINT activity_activities_speakers_fk FOREIGN KEY (activity_id) REFERENCES activities(id),
   CONSTRAINT speaker_activities_speakers_fk FOREIGN KEY (speaker_id) REFERENCES speakers(id)
)
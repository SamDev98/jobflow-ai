CREATE TABLE salary_researches (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    application_id  UUID REFERENCES applications(id),
    job_title       VARCHAR NOT NULL,
    company         VARCHAR,
    location        VARCHAR,
    range_low_usd   INT NOT NULL,
    range_mid_usd   INT NOT NULL,
    range_high_usd  INT NOT NULL,
    data_sources    TEXT,
    reasoning       TEXT,
    confidence_score INT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE interview_preps (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id  UUID REFERENCES applications(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES users(id),
    questions       TEXT NOT NULL,
    anki_deck_s3_key VARCHAR,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

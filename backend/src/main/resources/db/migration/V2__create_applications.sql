CREATE TABLE applications (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                 UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company                 VARCHAR NOT NULL,
    role                    VARCHAR NOT NULL,
    job_description         TEXT,
    jd_url                  TEXT,
    tech_stack              TEXT[],
    stage                   VARCHAR NOT NULL DEFAULT 'APPLIED',
    salary_range_low_usd    INT,
    salary_range_high_usd   INT,
    deadline                DATE,
    last_recruiter_contact  TIMESTAMP WITH TIME ZONE,
    awaiting_response       BOOLEAN NOT NULL DEFAULT FALSE,
    interview_datetime      TIMESTAMP WITH TIME ZONE,
    notes                   TEXT,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at              TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_applications_user_stage ON applications(user_id, stage)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_applications_deadline ON applications(deadline)
    WHERE deadline IS NOT NULL AND deleted_at IS NULL;

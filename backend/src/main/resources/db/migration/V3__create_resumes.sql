CREATE TABLE resumes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id  UUID REFERENCES applications(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES users(id),
    s3_key_docx     VARCHAR NOT NULL,
    s3_key_pdf      VARCHAR NOT NULL,
    ats_score       INT,
    changes_made    TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

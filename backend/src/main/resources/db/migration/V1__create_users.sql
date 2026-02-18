CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    clerk_id    VARCHAR UNIQUE NOT NULL,
    email       VARCHAR UNIQUE NOT NULL,
    tier        VARCHAR NOT NULL DEFAULT 'FREE',
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE user_profiles (
    user_id             UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    years_experience    INT,
    tech_stack          TEXT[],
    location            VARCHAR,
    salary_min_usd      INT,
    work_mode           VARCHAR,
    base_resume_s3_key  VARCHAR,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TYPE holiday_category AS ENUM (
    'Religious',
    'Secular',
    'Civil',
    'National',
    'Corporate'
);

CREATE TABLE holiday (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category holiday_category NOT NULL,
    start_date_epoch_day BIGINT NOT NULL,
    end_date_epoch_day BIGINT NOT NULL,
    location VARCHAR(255),
    custom BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_dates CHECK (start_date_epoch_day < end_date_epoch_day)
);

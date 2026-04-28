CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    user_name     VARCHAR(256) NOT NULL UNIQUE,
    password_hash TEXT         NOT NULL
);

CREATE TABLE IF NOT EXISTS stored_files
(
    id          BIGSERIAL PRIMARY KEY,
    file_name   VARCHAR(256) NOT NULL,
    path        VARCHAR(256) NOT NULL,
    upload_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id     BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE
);



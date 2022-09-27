create table POSTS (
    id bigserial NOT NULL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users("id"),
    post_text VARCHAR(150) NOT NULL,
    datatime TIMESTAMP NOT NULL
);

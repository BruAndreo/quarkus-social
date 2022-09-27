create table followers (
    id bigserial NOT NULL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users("id"),
    follower_id BIGINT NOT NULL REFERENCES users("id")
);
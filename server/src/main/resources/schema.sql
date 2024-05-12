CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  varchar(100),
    email varchar(320)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description  varchar(255),
    requester_id bigint,
    created      timestamp without time zone,
    CONSTRAINT fk_requests_to_users FOREIGN KEY (requester_id) REFERENCES users (id),
    UNIQUE (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         varchar(100),
    description  varchar(255),
    is_available boolean,
    user_id      BIGINT,
    request_id   BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_items_to_requests FOREIGN KEY (request_id) REFERENCES requests (id),
    UNIQUE (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date timestamp without time zone,
    end_date   timestamp without time zone,
    status     varchar(10),
    item_id    bigint,
    booker_id  bigint,
    CONSTRAINT fk_bookings_to_users FOREIGN KEY (booker_id) REFERENCES users (id),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY (item_id) REFERENCES items (id),
    UNIQUE (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text      varchar(255),
    item_id   bigint,
    author_id bigint,
    created   timestamp without time zone,
    CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY (author_id) REFERENCES users (id),
    UNIQUE (id)
);
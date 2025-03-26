CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        varchar(255)                            NOT NULL,
    description varchar(2000)                           NOT NULL,
    available   bool                                    NOT NULL,
    user_id     BIGINT                                  NOT NULL,
    request_id  BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT owner_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITH TIME ZONE                NOT NULL,
    end_date   TIMESTAMP WITH TIME ZONE                NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    user_id    BIGINT                                  NOT NULL,
    status     VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT item_id_booking_fk FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT user_id_booking_fk FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(2000)                           NOT NULL,
    item_id   BIGINT                                  NOT NULL,
    author_id BIGINT                                  NOT NULL,
    created   TIMESTAMP WITH TIME ZONE                NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT item_id_comment_fk FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT author_id_comment_fk FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS item_requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description varchar(2000)                           NOT NULL,
    user_id     BIGINT                                  NOT NULL,
    created     TIMESTAMP WITH TIME ZONE                NOT NULL,
    CONSTRAINT pk_item_request PRIMARY KEY (id),
    CONSTRAINT user_id_item_request_fk FOREIGN KEY (user_id) REFERENCES users (id)
);
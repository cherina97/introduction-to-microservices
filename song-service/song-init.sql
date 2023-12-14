CREATE TABLE IF NOT EXISTS song
(
    id          serial not null primary key,
    name        varchar,
    artist      varchar,
    album       varchar,
    duration    varchar,
    resource_id INT    not null,
    year        varchar
);
CREATE TABLE IF NOT EXISTS resource
(
    id   serial not null primary key,
    data bytea
);

drop table resource;
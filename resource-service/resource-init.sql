CREATE TABLE IF NOT EXISTS resource
(
    id           serial not null primary key,
    bucket       varchar,
    resource_key varchar
);
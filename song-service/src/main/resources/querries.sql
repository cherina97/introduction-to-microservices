CREATE TABLE IF NOT EXISTS song
(
    id          serial not null primary key,
    name        varchar(100),
    artist      varchar(100),
    album       varchar(100),
    duration    varchar(100),
    resource_id INT    not null,
    year        varchar(4)
);
CREATE TABLE IF NOT EXISTS song
(
    id          serial not null primary key,
    name        varchar(100),
    artist      varchar(80),
    album       varchar(100),
    duration    varchar(5),
    resource_id INT    not null,
    year        varchar(4)
);
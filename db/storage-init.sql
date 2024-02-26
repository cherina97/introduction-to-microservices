CREATE TABLE IF NOT EXISTS storages
(
    id           serial not null primary key,
    storage_type varchar,
    bucket       varchar,
    path         varchar
);

insert into storages (id, storage_type, bucket, path)
values (1, 'STAGING', 'staging', '/staging'),
       (2, 'PERMANENT', 'permanent', '/permanent');
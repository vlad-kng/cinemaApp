create table actor
(
    id   integer generated by default as identity
        primary key,
    name varchar(50) not null
);
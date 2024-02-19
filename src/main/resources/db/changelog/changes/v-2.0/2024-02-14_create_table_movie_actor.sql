create table if not exists movie_actor
(
    movie_id integer not null
        references movie,
    actor_id integer not null
        references actor,
    primary key (movie_id, actor_id)
);
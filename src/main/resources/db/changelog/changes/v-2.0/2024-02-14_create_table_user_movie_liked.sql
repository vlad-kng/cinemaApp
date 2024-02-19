create table if not exists user_movie_liked
(
    user_id  integer not null
        references user_profile,
    movie_id integer not null
        references movie,
    primary key (user_id, movie_id)
);
create table user_movie_not_interested
(
    user_id  integer
        references user_profile,
    movie_id integer
        references movie
);
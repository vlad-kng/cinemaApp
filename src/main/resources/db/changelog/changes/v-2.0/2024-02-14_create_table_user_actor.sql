create table if not exists user_actor
(
    user_id  integer
        references user_profile,
    actor_id integer
        references actor
);
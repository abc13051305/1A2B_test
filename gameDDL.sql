-- auto-generated definition
create table game
(
    uuid             varchar(36) not null
        constraint game_pk
            primary key,
    player1_id       varchar(36) not null,
    player2_id       varchar(36) not null,
    turn_player      varchar(25),
    state            varchar(25) not null,
    rounds           integer     not null,
    guess_history_id varchar(36),
    create_date_time timestamp default now(),
    update_date_time timestamp default now()
);

comment on column game.turn_player is 'player1 or player2';

comment on column game.state is '遊戲狀態';

comment on column game.rounds is '遊戲回合';

alter table game
    owner to tcfd;
	
-- auto-generated definition
create table guess_history
(
    uuid             varchar(36)             not null
        constraint guess_history_pk
            primary key,
    guess_number     varchar(4)              not null,
    answer           varchar(4)              not null,
    player_id        varchar(36)             not null,
    game_id          varchar(36)             not null,
    result           varchar(4)              not null,
    update_date_time timestamp default now() not null,
    create_date_time timestamp default now() not null,
    round            integer                 not null
);

comment on column guess_history.guess_number is '猜的數字';

comment on column guess_history.result is '遊戲結果';

alter table guess_history
    owner to tcfd;
	
-- auto-generated definition
create table player
(
    uuid             varchar(36)             not null
        constraint player_pk
            primary key,
    game_id          varchar(36)             not null,
    create_date_time timestamp default now() not null,
    update_date_time timestamp default now() not null,
    answer           varchar(4)
);

alter table player
    owner to tcfd;



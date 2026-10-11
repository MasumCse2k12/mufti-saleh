create table schedule_event (
    id          uuid        not null default gen_random_uuid() primary key,
    title       text        not null,
    description text,
    event_at    timestamptz not null,
    location    varchar(256),
    type        varchar(16) not null default 'PROGRAM',
    created_at  timestamptz not null,
    updated_at  timestamptz
);

create index schedule_event_at_idx on schedule_event (event_at asc);

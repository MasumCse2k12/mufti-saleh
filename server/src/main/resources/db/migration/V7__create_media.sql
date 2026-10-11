create table media_item (
    id               uuid        not null default gen_random_uuid() primary key,
    title            text        not null,
    description      text,
    type             varchar(16) not null default 'BAYAAN',
    youtube_id       varchar(20),
    media_url        text,
    duration_seconds integer,
    published_at     timestamptz not null,
    created_at       timestamptz not null,
    updated_at       timestamptz
);

create index media_item_type_idx on media_item (type);
create index media_item_published_at_idx on media_item (published_at desc);

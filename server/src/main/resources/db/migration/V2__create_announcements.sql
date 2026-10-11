create table announcement (
    id           uuid        not null default gen_random_uuid() primary key,
    title        text        not null,
    body         text        not null,
    pinned       boolean     not null default false,
    published_at timestamptz not null,
    expires_at   timestamptz,
    created_at   timestamptz not null,
    updated_at   timestamptz
);

create index announcement_published_at_idx on announcement (published_at desc);

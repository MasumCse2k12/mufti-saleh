create table bayah_request (
    id          uuid        not null default gen_random_uuid() primary key,
    name        varchar(128) not null,
    phone       varchar(20),
    district    varchar(64),
    note        text,
    status      varchar(16) not null default 'NEW',
    admin_note  text,
    created_at  timestamptz not null,
    updated_at  timestamptz
);

create index bayah_status_idx on bayah_request (status);
create index bayah_created_at_idx on bayah_request (created_at desc);

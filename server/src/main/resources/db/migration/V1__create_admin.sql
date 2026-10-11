create table admin (
    id               uuid        not null default gen_random_uuid() primary key,
    username         varchar(64) not null,
    display_name     varchar(128) not null,
    password_hash    text        not null,
    role             varchar(16) not null default 'EDITOR',
    active           boolean     not null default true,
    must_change_password boolean not null default true,
    last_login_at    timestamptz,
    failed_attempts  integer     not null default 0,
    locked_until     timestamptz,
    created_at       timestamptz not null,
    updated_at       timestamptz
);

create unique index admin_username_uidx on admin (lower(username));

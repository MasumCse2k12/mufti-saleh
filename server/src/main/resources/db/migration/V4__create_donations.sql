create table donation (
    id          uuid           not null default gen_random_uuid() primary key,
    donor_name  varchar(128),
    amount      numeric(12, 2) not null,
    purpose     varchar(32)    not null,
    note        text,
    created_at  timestamptz    not null,
    updated_at  timestamptz
);

create index donation_purpose_idx on donation (purpose);
create index donation_created_at_idx on donation (created_at desc);

create table question (
    id                 uuid        not null default gen_random_uuid() primary key,
    body               text        not null,
    category           varchar(32) not null default 'GENERAL',
    status             varchar(16) not null default 'PENDING',
    answer             text,
    answered_at        timestamptz,
    submitter_name     varchar(128),
    submitter_contact  varchar(128),
    created_at         timestamptz not null,
    updated_at         timestamptz
);

create index question_status_idx on question (status);
create index question_category_idx on question (category);
create index question_answered_at_idx on question (answered_at desc) where answered_at is not null;

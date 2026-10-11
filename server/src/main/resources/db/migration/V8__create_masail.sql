create table masail (
    id         uuid        not null default gen_random_uuid() primary key,
    question   text        not null,
    answer     text        not null,
    category   varchar(32) not null default 'MISC',
    published  boolean     not null default true,
    created_at timestamptz not null,
    updated_at timestamptz
);

create index masail_category_idx on masail (category);
create index masail_published_idx on masail (published) where published = true;

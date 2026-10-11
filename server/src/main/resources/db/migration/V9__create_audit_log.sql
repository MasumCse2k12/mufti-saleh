create table audit_log (
    id          bigserial   not null primary key,
    at          timestamptz not null,
    action      varchar(32) not null,
    entity      text        not null,
    entity_id   text        not null,
    actor_id    uuid,
    actor_kind  varchar(16) not null,
    actor_label text        not null,
    note        text,
    changes     jsonb       not null default '{}',
    request_id  varchar(64),
    ip          varchar(45),
    method      varchar(10),
    path        text
);

create index audit_log_at_idx       on audit_log (at desc, id desc);
create index audit_log_entity_idx   on audit_log (entity, entity_id);
create index audit_log_actor_id_idx on audit_log (actor_id) where actor_id is not null;

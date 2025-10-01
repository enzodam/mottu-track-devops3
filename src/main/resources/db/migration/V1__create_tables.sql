-- FILIAL
create table if not exists filial (
  id      bigserial primary key,
  nome    varchar(120) not null,
  estado  char(2) not null
);

-- USUARIO
create table if not exists usuario (
  id        bigserial primary key,
  username  varchar(80)  not null unique,
  senha     varchar(150) not null,
  nome      varchar(120) not null,
  email     varchar(120) not null unique,
  perfil    varchar(20)  not null,    -- 'ADMIN' | 'USER'
  ativo     boolean      not null default true
);

-- PATIO
create table if not exists patio (
  id        bigserial primary key,
  nome      varchar(80) not null,
  filial_id bigint      not null references filial(id)
);

-- VAGA
create table if not exists vaga (
  id         bigserial primary key,
  codigo     varchar(20) not null unique,
  disponivel boolean     not null default true,
  patio_id   bigint      not null references patio(id)
);

-- MOTO
create table if not exists moto (
  id         bigserial primary key,
  placa      varchar(10) not null unique,
  cor        varchar(20) not null,
  disponivel boolean     not null default true,
  filial_id  bigint      not null references filial(id)
);

-- =========================
-- V3: Índices e Constraints
-- =========================

-- Índices para FK (melhora performance nas listas e joins)
create index if not exists idx_patio_filial on patio(filial_id);
create index if not exists idx_vaga_patio   on vaga(patio_id);
create index if not exists idx_moto_filial  on moto(filial_id);

-- Garante unicidade (caso não tenha sido definida no V1)
do $$
begin
    if not exists (
        select 1 from information_schema.table_constraints
        where table_name = 'moto' and constraint_name = 'uq_moto_placa'
    ) then
        alter table moto add constraint uq_moto_placa unique (placa);
    end if;

    if not exists (
        select 1 from information_schema.table_constraints
        where table_name = 'vaga' and constraint_name = 'uq_vaga_codigo'
    ) then
        alter table vaga add constraint uq_vaga_codigo unique (codigo);
    end if;

    if not exists (
        select 1 from information_schema.table_constraints
        where table_name = 'usuario' and constraint_name = 'uq_usuario_username'
    ) then
        alter table usuario add constraint uq_usuario_username unique (username);
    end if;

    if not exists (
        select 1 from information_schema.table_constraints
        where table_name = 'usuario' and constraint_name = 'uq_usuario_email'
    ) then
        alter table usuario add constraint uq_usuario_email unique (email);
    end if;
end
$$;

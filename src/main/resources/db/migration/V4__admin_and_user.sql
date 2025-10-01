-- ======================================
-- V4: Usuários padrão (ADMIN e USER demo)
-- ======================================

-- Troque o hash se quiser outras senhas.
-- Este hash é um exemplo de BCrypt (10 rounds).
-- Você já usou esse hash no projeto; mantive para consistência.
-- Recomendo usar PasswordEncoder na aplicação para logins reais.

insert into usuario (username, senha, nome, email, perfil, ativo) values
('admin', '$2a$10$W0GmHTaQ8qHn6weY4nVJYevjF6bM3Q6gH7/6jM7YfV9q9K8tC3rS6', 'Administrador', 'admin@mottu.com', 'ADMIN', true)
on conflict (username) do nothing;

insert into usuario (username, senha, nome, email, perfil, ativo) values
('user',  '$2a$10$W0GmHTaQ8qHn6weY4nVJYevjF6bM3Q6gH7/6jM7YfV9q9K8tC3rS6', 'Usuário Demo',  'user@mottu.com',  'USER',  true)
on conflict (username) do nothing;

-- Filiais iniciais
insert into filial (nome, estado) values
('São Paulo - Centro', 'SP'),
('Campinas - Taquaral', 'SP');

-- Pátios iniciais
insert into patio (nome, filial_id) values
('Pátio Central', 1),
('Pátio Taquaral', 2);

-- Vagas iniciais
insert into vaga (codigo, disponivel, patio_id) values
('A-01', true, 1),
('A-02', true, 1),
('B-01', true, 2);

-- Motos iniciais
insert into moto (placa, cor, disponivel, filial_id) values
('ABC1D23', 'Preta',  true, 1),
('EFG4H56', 'Verde',  true, 2);

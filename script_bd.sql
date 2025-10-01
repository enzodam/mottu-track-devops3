-- script_bd.sql
-- Projeto: challenge3-devops | RM: 558438
-- Estrutura da tabela principal da aplicação: motos (CRUD completo)

IF OBJECT_ID('dbo.motos','U') IS NOT NULL DROP TABLE dbo.motos;
GO

CREATE TABLE dbo.motos (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    placa        VARCHAR(10)  NOT NULL UNIQUE,
    modelo       VARCHAR(100) NOT NULL,
    ano          INT          NOT NULL CHECK (ano BETWEEN 1900 AND YEAR(GETDATE()) + 1),
    disponivel   BIT          NOT NULL,
    criado_em    DATETIME2    NOT NULL DEFAULT SYSUTCDATETIME(),
    atualizado_em DATETIME2   NULL
);
GO

-- 2 registros reais
INSERT INTO dbo.motos (placa, modelo, ano, disponivel) VALUES
('ABC1D23', 'Honda CG 160', 2022, 1),
('XYZ9Z99', 'Yamaha Fazer 250', 2024, 0);
GO

SELECT TOP 10 * FROM dbo.motos ORDER BY id DESC;
GO

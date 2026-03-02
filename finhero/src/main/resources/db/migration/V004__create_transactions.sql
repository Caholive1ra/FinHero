-- Migration: Criar tabela transactions
-- Descrição: Tabela para armazenar transações financeiras (receitas e despesas)

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(10) NOT NULL CHECK (type IN ('RECEITA', 'DESPESA')),
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    description TEXT,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_transactions_category_id ON transactions(category_id);


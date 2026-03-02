-- Migration: Criar tabela duplas
-- Descrição: Tabela para armazenar vínculos de dupla financeira entre usuários

CREATE TABLE IF NOT EXISTS duplas (
    id BIGSERIAL PRIMARY KEY,
    user_a_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_b_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_a_id, user_b_id),
    CHECK (user_a_id < user_b_id) -- Impede duplicatas (A,B) = (B,A)
);

CREATE INDEX IF NOT EXISTS idx_duplas_user_a ON duplas(user_a_id);
CREATE INDEX IF NOT EXISTS idx_duplas_user_b ON duplas(user_b_id);


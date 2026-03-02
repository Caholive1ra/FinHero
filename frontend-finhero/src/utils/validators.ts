// Validações do frontend

export const validateEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

export const validatePassword = (password: string): { valid: boolean; message?: string } => {
  if (password.length < 6) {
    return { valid: false, message: 'A senha deve ter pelo menos 6 caracteres' };
  }
  return { valid: true };
};

export const validateAmount = (amount: number): { valid: boolean; message?: string } => {
  if (amount <= 0) {
    return { valid: false, message: 'O valor deve ser maior que zero' };
  }
  if (amount > 999999999.99) {
    return { valid: false, message: 'O valor é muito grande' };
  }
  return { valid: true };
};

export const validateInviteCode = (code: string): { valid: boolean; message?: string } => {
  if (!code || code.length !== 8) {
    return { valid: false, message: 'O código de convite deve ter 8 caracteres' };
  }
  return { valid: true };
};


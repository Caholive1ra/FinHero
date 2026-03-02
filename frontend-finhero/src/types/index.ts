// Tipos baseados nos DTOs e Models do backend

export interface User {
  id: number;
  email: string;
  inviteCode: string;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginDTO {
  email: string;
  password: string;
}

export interface RegisterDTO {
  email: string;
  password: string;
}

export type TransactionType = 'RECEITA' | 'DESPESA';

export interface Transaction {
  id: number;
  type: TransactionType;
  amount: number;
  description: string | null;
  categoryId: number;
  userId: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTransactionDTO {
  type: TransactionType;
  amount: number;
  description?: string;
  categoryId: number;
}

export interface Category {
  id: number;
  name: string;
  userId: number;
  createdAt: string;
}

export interface Dupla {
  id: number;
  userAId: number;
  userBId: number;
  createdAt: string;
}

export interface LinkDuplaDTO {
  inviteCode: string;
}

export interface ApiError {
  error: string;
  message?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

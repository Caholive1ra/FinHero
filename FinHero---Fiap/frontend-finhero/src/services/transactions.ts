import api from './api';
import type { Transaction, CreateTransactionDTO, PaginatedResponse } from '../types';

export const transactionService = {
  async create(data: CreateTransactionDTO): Promise<Transaction> {
    const response = await api.post<Transaction>('/transactions', data);
    return response.data;
  },

  async getAll(page: number = 0, size: number = 20): Promise<PaginatedResponse<Transaction>> {
    const response = await api.get<PaginatedResponse<Transaction>>('/transactions', {
      params: { page, size },
    });
    return response.data;
  },
};


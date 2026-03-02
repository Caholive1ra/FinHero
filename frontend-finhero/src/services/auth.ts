import api from './api';
import type { AuthResponse, LoginDTO, RegisterDTO, User } from '../types';
import { STORAGE_KEYS } from '../utils/constants';

export const authService = {
  async register(data: RegisterDTO): Promise<User> {
    const response = await api.post<User>('/auth/register', data);
    return response.data;
  },

  async login(data: LoginDTO): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/auth/login', data);
    
    // Salvar token e usuário
    if (response.data.token && response.data.user) {
      localStorage.setItem(STORAGE_KEYS.TOKEN, response.data.token);
      localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(response.data.user));
    }
    
    return response.data;
  },

  async getCurrentUser(): Promise<User> {
    const response = await api.get<User>('/users/me');
    return response.data;
  },

  logout(): void {
    localStorage.removeItem(STORAGE_KEYS.TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER);
    localStorage.removeItem(STORAGE_KEYS.XP);
    localStorage.removeItem(STORAGE_KEYS.LEVEL);
  },

  getToken(): string | null {
    return localStorage.getItem(STORAGE_KEYS.TOKEN);
  },

  isAuthenticated(): boolean {
    return !!this.getToken();
  },
};


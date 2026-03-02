import api from './api';
import type { Dupla, LinkDuplaDTO } from '../types';

export const duplaService = {
  async link(data: LinkDuplaDTO): Promise<Dupla> {
    const response = await api.post<Dupla>('/dupla/link', data);
    return response.data;
  },
};


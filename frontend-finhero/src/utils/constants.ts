// Constantes do aplicativo

export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export const STORAGE_KEYS = {
  TOKEN: 'finhero_token',
  USER: 'finhero_user',
  THEME: 'finhero_theme',
  XP: 'finhero_xp',
  LEVEL: 'finhero_level',
  ACHIEVEMENTS: 'finhero_achievements',
} as const;

// Sistema de XP (apenas frontend)
export const XP_VALUES = {
  CREATE_TRANSACTION: 10,
  CREATE_5_TRANSACTIONS: 50,
  CREATE_10_TRANSACTIONS: 100,
  LINK_DUPLA: 100,
  SEVEN_DAY_STREAK: 200,
  COMPLETE_PROFILE: 50,
  FIRST_TRANSACTION: 25,
} as const;

export const XP_FOR_LEVEL = (level: number): number => {
  return 100 * level * (level + 1) / 2;
};

// Conquistas
export type Achievement = {
  id: string;
  name: string;
  description: string;
  icon: string;
  xpReward: number;
}

export const ACHIEVEMENTS: Achievement[] = [
  {
    id: 'first-step',
    name: 'Primeiro Passo',
    description: 'Cadastre sua primeira transação',
    icon: '🎯',
    xpReward: 25,
  },
  {
    id: 'organized',
    name: 'Organizado',
    description: 'Cadastre 10 transações',
    icon: '📊',
    xpReward: 50,
  },
  {
    id: 'finance-master',
    name: 'Mestre das Finanças',
    description: 'Cadastre 100 transações',
    icon: '💰',
    xpReward: 200,
  },
  {
    id: 'perfect-dupla',
    name: 'Dupla Perfeita',
    description: 'Vincule-se a um parceiro',
    icon: '👫',
    xpReward: 100,
  },
  {
    id: 'faithful',
    name: 'Fiel',
    description: 'Mantenha um streak de 7 dias',
    icon: '🔥',
    xpReward: 200,
  },
  {
    id: 'economist',
    name: 'Economista',
    description: 'Tenha mais receitas que despesas em um mês',
    icon: '💸',
    xpReward: 150,
  },
  {
    id: 'balanced',
    name: 'Equilibrado',
    description: 'Mantenha despesas abaixo de 80% da receita',
    icon: '⚖️',
    xpReward: 150,
  },
  {
    id: 'champion',
    name: 'Campeão',
    description: 'Seja nível 10 ou superior',
    icon: '🏆',
    xpReward: 300,
  },
  {
    id: 'analysis-complete',
    name: 'Análise Completa',
    description: 'Visualize gráficos por 10 dias seguidos',
    icon: '📈',
    xpReward: 100,
  },
  {
    id: 'trusted-partner',
    name: 'Parceiro Confiável',
    description: 'Dupla ativa por 30 dias',
    icon: '🤝',
    xpReward: 250,
  },
];

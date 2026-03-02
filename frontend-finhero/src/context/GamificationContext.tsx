import { createContext, useContext, useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import { STORAGE_KEYS, XP_FOR_LEVEL, ACHIEVEMENTS } from '../utils/constants';
import type { Achievement } from '../utils/constants';
import { storage } from '../utils/storage';

interface GamificationContextType {
  xp: number;
  level: number;
  achievements: string[];
  addXP: (amount: number, reason?: string) => void;
  checkAchievements: (actions: Record<string, number>) => Promise<string[]>;
  getProgressToNextLevel: () => number;
  getUnlockedAchievements: () => Achievement[];
  reset: () => void;
}

const GamificationContext = createContext<GamificationContextType | undefined>(undefined);

export const GamificationProvider = ({ children }: { children: ReactNode }) => {
  const [xp, setXp] = useState(() => storage.get<number>(STORAGE_KEYS.XP, 0) ?? 0);
  const [achievements, setAchievements] = useState<string[]>(
    () => storage.get<string[]>(STORAGE_KEYS.ACHIEVEMENTS, []) ?? []
  );

  const calculateLevel = (totalXP: number): number => {
    let level = 1;
    while (XP_FOR_LEVEL(level + 1) <= totalXP) {
      level++;
    }
    return level;
  };

  const level = calculateLevel(xp);

  useEffect(() => {
    storage.set(STORAGE_KEYS.XP, xp);
    storage.set(STORAGE_KEYS.LEVEL, level);
  }, [xp, level]);

  useEffect(() => {
    storage.set(STORAGE_KEYS.ACHIEVEMENTS, achievements);
  }, [achievements]);

  const addXP = (amount: number, _reason?: string) => {
    setXp((prev) => {
      const newXP = prev + amount;
      // Verificar se subiu de nível
      const newLevel = calculateLevel(newXP);
      if (newLevel > level) {
        // Animação de level up pode ser adicionada aqui
        console.log(`🎉 Level Up! Nível ${newLevel}`);
      }
      return newXP;
    });
  };

  const checkAchievements = async (actions: Record<string, number>): Promise<string[]> => {
    const newlyUnlocked: string[] = [];

    // Verificar conquistas baseadas em ações
    if (actions.transactionCount === 1 && !achievements.includes('first-step')) {
      newlyUnlocked.push('first-step');
    }
    if (actions.transactionCount >= 10 && !achievements.includes('organized')) {
      newlyUnlocked.push('organized');
    }
    if (actions.transactionCount >= 100 && !achievements.includes('finance-master')) {
      newlyUnlocked.push('finance-master');
    }
    if (actions.hasDupla && !achievements.includes('perfect-dupla')) {
      newlyUnlocked.push('perfect-dupla');
    }
    if (level >= 10 && !achievements.includes('champion')) {
      newlyUnlocked.push('champion');
    }

    if (newlyUnlocked.length > 0) {
      setAchievements((prev) => [...prev, ...newlyUnlocked]);
      // Adicionar XP das conquistas
      newlyUnlocked.forEach((achId) => {
        const achievement = ACHIEVEMENTS.find((a) => a.id === achId);
        if (achievement) {
          addXP(achievement.xpReward);
        }
      });
    }

    return newlyUnlocked;
  };

  const getProgressToNextLevel = (): number => {
    const currentLevelXP = XP_FOR_LEVEL(level);
    const nextLevelXP = XP_FOR_LEVEL(level + 1);
    const progressXP = xp - currentLevelXP;
    const neededXP = nextLevelXP - currentLevelXP;
    return neededXP > 0 ? (progressXP / neededXP) * 100 : 100;
  };

  const getUnlockedAchievements = (): Achievement[] => {
    return ACHIEVEMENTS.filter((ach) => achievements.includes(ach.id));
  };

  const reset = () => {
    setXp(0);
    setAchievements([]);
    storage.remove(STORAGE_KEYS.XP);
    storage.remove(STORAGE_KEYS.LEVEL);
    storage.remove(STORAGE_KEYS.ACHIEVEMENTS);
  };

  const value: GamificationContextType = {
    xp,
    level,
    achievements,
    addXP,
    checkAchievements,
    getProgressToNextLevel,
    getUnlockedAchievements,
    reset,
  };

  return (
    <GamificationContext.Provider value={value}>
      {children}
    </GamificationContext.Provider>
  );
};

export const useGamification = () => {
  const context = useContext(GamificationContext);
  if (context === undefined) {
    throw new Error('useGamification must be used within a GamificationProvider');
  }
  return context;
};


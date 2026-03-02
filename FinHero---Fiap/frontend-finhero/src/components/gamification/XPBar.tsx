import { motion } from 'framer-motion';
import { useGamification } from '../../context/GamificationContext';
import { Trophy } from 'lucide-react';

export const XPBar = () => {
  const { xp, level, getProgressToNextLevel } = useGamification();
  const progress = getProgressToNextLevel();

  return (
    <div className="space-y-2">
      <div className="flex items-center justify-between text-sm">
        <div className="flex items-center gap-2">
          <Trophy className="w-5 h-5 text-warning" />
          <span className="font-bold text-gray-900 dark:text-gray-100">
            Nível {level}
          </span>
        </div>
        <span className="text-gray-600 dark:text-gray-400 font-medium">
          {xp.toLocaleString('pt-BR')} XP
        </span>
      </div>
      <div className="w-full bg-gray-200 dark:bg-slate-700 rounded-full h-3 overflow-hidden">
        <motion.div
          className="h-full bg-gradient-to-r from-primary via-secondary to-primary"
          initial={{ width: 0 }}
          animate={{ width: `${progress}%` }}
          transition={{ duration: 0.5, ease: 'easeOut' }}
        />
      </div>
    </div>
  );
};


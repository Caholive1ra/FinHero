import { motion, AnimatePresence } from 'framer-motion';
import type { Achievement } from '../../utils/constants';
import { X } from 'lucide-react';

interface AchievementUnlockProps {
  achievement: Achievement | null;
  onClose: () => void;
}

export const AchievementUnlock = ({ achievement, onClose }: AchievementUnlockProps) => {
  return (
    <AnimatePresence>
      {achievement && (
        <>
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/50 dark:bg-black/70 backdrop-blur-sm z-50"
            onClick={onClose}
          />
          <div className="fixed inset-0 flex items-center justify-center z-50 p-4">
            <motion.div
              initial={{ opacity: 0, scale: 0.5, rotate: -180 }}
              animate={{ opacity: 1, scale: 1, rotate: 0 }}
              exit={{ opacity: 0, scale: 0.5, rotate: 180 }}
              transition={{ type: 'spring', stiffness: 200, damping: 20 }}
              className="bg-gradient-to-br from-primary via-secondary to-success p-8 rounded-2xl shadow-2xl max-w-md w-full relative overflow-hidden"
              onClick={(e) => e.stopPropagation()}
            >
              <button
                onClick={onClose}
                className="absolute top-4 right-4 p-2 rounded-lg bg-white/20 hover:bg-white/30 transition-colors"
                aria-label="Fechar"
              >
                <X className="w-5 h-5 text-white" />
              </button>

              <div className="text-center space-y-4">
                <motion.div
                  initial={{ scale: 0 }}
                  animate={{ scale: [0, 1.2, 1] }}
                  transition={{ delay: 0.2, type: 'spring' }}
                  className="text-7xl"
                >
                  {achievement.icon}
                </motion.div>

                <motion.div
                  initial={{ y: 20, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.3 }}
                >
                  <h2 className="text-3xl font-bold text-white mb-2">
                    🎉 Conquista Desbloqueada!
                  </h2>
                  <h3 className="text-2xl font-bold text-white mb-2">
                    {achievement.name}
                  </h3>
                  <p className="text-white/90 text-lg">
                    {achievement.description}
                  </p>
                </motion.div>

                <motion.div
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                  transition={{ delay: 0.5, type: 'spring' }}
                  className="inline-block bg-white/20 backdrop-blur-sm px-4 py-2 rounded-full"
                >
                  <span className="text-white font-bold text-lg">
                    +{achievement.xpReward} XP
                  </span>
                </motion.div>
              </div>

              {/* Confetti animation */}
              {[...Array(12)].map((_, i) => (
                <motion.div
                  key={i}
                  className="absolute w-2 h-2 bg-white rounded-full"
                  initial={{
                    x: '50%',
                    y: '50%',
                    opacity: 1,
                  }}
                  animate={{
                    x: `${50 + (Math.random() - 0.5) * 100}%`,
                    y: `${50 + (Math.random() - 0.5) * 100}%`,
                    opacity: 0,
                  }}
                  transition={{
                    duration: 1.5,
                    delay: 0.5 + i * 0.1,
                  }}
                />
              ))}
            </motion.div>
          </div>
        </>
      )}
    </AnimatePresence>
  );
};


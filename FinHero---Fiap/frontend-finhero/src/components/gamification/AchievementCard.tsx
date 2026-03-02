import { motion } from 'framer-motion';
import type { Achievement } from '../../utils/constants';
import { Card } from '../common/Card';

interface AchievementCardProps {
  achievement: Achievement;
  unlocked: boolean;
}

export const AchievementCard = ({ achievement, unlocked }: AchievementCardProps) => {
  return (
    <Card
      className={`relative overflow-hidden ${unlocked ? '' : 'opacity-50 grayscale'}`}
      hover={unlocked}
    >
      <div className="flex items-center gap-4">
        <div className="text-4xl">{achievement.icon}</div>
        <div className="flex-1">
          <h3 className="font-bold text-gray-900 dark:text-gray-100">
            {achievement.name}
          </h3>
          <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
            {achievement.description}
          </p>
          {unlocked && (
            <motion.span
              initial={{ opacity: 0, scale: 0 }}
              animate={{ opacity: 1, scale: 1 }}
              className="inline-block mt-2 badge badge-success"
            >
              +{achievement.xpReward} XP
            </motion.span>
          )}
        </div>
      </div>
      {unlocked && (
        <motion.div
          className="absolute top-0 right-0 w-20 h-20 bg-gradient-to-br from-success/20 to-success/5 rounded-bl-full"
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          transition={{ delay: 0.2 }}
        />
      )}
    </Card>
  );
};


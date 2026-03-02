import { useEffect, useState } from 'react';
import { Layout } from '../components/common/Layout';
import { Card } from '../components/common/Card';
import { Loading } from '../components/common/Loading';
import { XPBar } from '../components/gamification/XPBar';
import { AchievementCard } from '../components/gamification/AchievementCard';
import { useAuth } from '../context/AuthContext';
import { useGamification } from '../context/GamificationContext';
import { transactionService } from '../services/transactions';
import { ACHIEVEMENTS } from '../utils/constants';
import { formatDate } from '../utils/formatters';
import { User, Copy, Check } from 'lucide-react';
import { useToast } from '../context/ToastContext';

export const Profile = () => {
  const { user } = useAuth();
  const { xp, level, getUnlockedAchievements } = useGamification();
  const { showToast } = useToast();
  const [loading, setLoading] = useState(true);
  const [transactionCount, setTransactionCount] = useState(0);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const response = await transactionService.getAll(0, 1);
      setTransactionCount(response.totalElements);
    } catch (error) {
      console.error('Erro ao carregar estatísticas:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCopyInviteCode = () => {
    if (user?.inviteCode) {
      navigator.clipboard.writeText(user.inviteCode);
      setCopied(true);
      showToast('Código de convite copiado!', 'success');
      setTimeout(() => setCopied(false), 2000);
    }
  };

  if (loading) {
    return (
      <Layout>
        <Loading fullScreen />
      </Layout>
    );
  }

  const unlockedAchievements = getUnlockedAchievements();

  return (
    <Layout>
      <div className="space-y-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100">
          Perfil
        </h1>

        {/* User Info */}
        <Card>
          <div className="flex items-center gap-4 mb-6">
            <div className="p-4 bg-primary/10 rounded-full">
              <User className="w-8 h-8 text-primary" />
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100">
                {user?.email}
              </h2>
              <p className="text-sm text-gray-600 dark:text-gray-400">
                Membro desde {user?.createdAt && formatDate(user.createdAt)}
              </p>
            </div>
          </div>

          {/* XP Bar */}
          <div className="mb-6">
            <XPBar />
          </div>

          {/* Invite Code */}
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Código de Convite
            </label>
            <div className="flex items-center gap-2">
              <Input
                value={user?.inviteCode || ''}
                readOnly
                className="font-mono text-lg font-bold"
              />
              <button
                onClick={handleCopyInviteCode}
                className="p-2 bg-gray-200 dark:bg-slate-700 hover:bg-gray-300 dark:hover:bg-slate-600 rounded-lg transition-colors"
                aria-label="Copiar código"
              >
                {copied ? (
                  <Check className="w-5 h-5 text-success" />
                ) : (
                  <Copy className="w-5 h-5 text-gray-600 dark:text-gray-400" />
                )}
              </button>
            </div>
            <p className="mt-2 text-sm text-gray-600 dark:text-gray-400">
              Compartilhe este código para formar uma dupla
            </p>
          </div>
        </Card>

        {/* Statistics */}
        <Card>
          <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">
            Estatísticas
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="p-4 bg-gray-50 dark:bg-slate-700 rounded-lg">
              <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                Total de Transações
              </p>
              <p className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                {transactionCount}
              </p>
            </div>
            <div className="p-4 bg-gray-50 dark:bg-slate-700 rounded-lg">
              <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                Nível Atual
              </p>
              <p className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                {level}
              </p>
            </div>
            <div className="p-4 bg-gray-50 dark:bg-slate-700 rounded-lg">
              <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                XP Total
              </p>
              <p className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                {xp.toLocaleString('pt-BR')}
              </p>
            </div>
          </div>
        </Card>

        {/* Achievements */}
        <Card>
          <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">
            Conquistas ({unlockedAchievements.length}/{ACHIEVEMENTS.length})
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {ACHIEVEMENTS.map((achievement) => (
              <AchievementCard
                key={achievement.id}
                achievement={achievement}
                unlocked={unlockedAchievements.some((a) => a.id === achievement.id)}
              />
            ))}
          </div>
        </Card>
      </div>
    </Layout>
  );
};

// Import necessário
import { Input } from '../components/common/Input';


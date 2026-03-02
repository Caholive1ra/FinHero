import { useState } from 'react';
import { motion } from 'framer-motion';
import { Layout } from '../components/common/Layout';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { Input } from '../components/common/Input';
import { duplaService } from '../services/dupla';
import { useToast } from '../context/ToastContext';
import { useGamification } from '../context/GamificationContext';
import { validateInviteCode } from '../utils/validators';
import { Users, UserCheck } from 'lucide-react';

export const Dupla = () => {
  const { showToast } = useToast();
  const { addXP, checkAchievements } = useGamification();
  const [inviteCode, setInviteCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [hasDupla, setHasDupla] = useState(false);

  const handleLink = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    const validation = validateInviteCode(inviteCode);
    if (!validation.valid) {
      setError(validation.message || 'Código inválido');
      return;
    }

    setLoading(true);
    try {
      await duplaService.link({ inviteCode });
      showToast('Dupla vinculada com sucesso!', 'success');
      addXP(100, 'Vincular dupla');
      await checkAchievements({
        transactionCount: 0,
      });
      setHasDupla(true);
      setInviteCode('');
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.error ||
        error.response?.data?.message ||
        'Erro ao vincular dupla';
      setError(errorMessage);
      showToast(errorMessage, 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="space-y-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100">
          Dupla
        </h1>

        {hasDupla ? (
          <Card>
            <div className="text-center py-8 space-y-4">
              <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                transition={{ delay: 0.2, type: 'spring' }}
                className="flex justify-center"
              >
                <div className="p-6 bg-success/10 rounded-full">
                  <UserCheck className="w-16 h-16 text-success" />
                </div>
              </motion.div>
              <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                Dupla Formada!
              </h2>
              <p className="text-gray-600 dark:text-gray-400">
                Você já está vinculado a uma dupla. Funcionalidades de comparação e rankings estarão disponíveis em breve.
              </p>
            </div>
          </Card>
        ) : (
          <Card>
            <div className="space-y-6">
              <div className="text-center space-y-2">
                <div className="flex justify-center">
                  <div className="p-4 bg-primary/10 rounded-full">
                    <Users className="w-12 h-12 text-primary" />
                  </div>
                </div>
                <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100">
                  Vincular Dupla
                </h2>
                <p className="text-gray-600 dark:text-gray-400">
                  Digite o código de convite do seu parceiro para formar uma dupla
                </p>
              </div>

              <form onSubmit={handleLink} className="space-y-4">
                <Input
                  label="Código de Convite"
                  value={inviteCode}
                  onChange={(e) => {
                    setInviteCode(e.target.value.toUpperCase());
                    setError('');
                  }}
                  error={error}
                  placeholder="ABC12345"
                  maxLength={8}
                  disabled={loading}
                />

                <Button
                  type="submit"
                  variant="primary"
                  fullWidth
                  disabled={loading || !inviteCode}
                >
                  {loading ? 'Vinculando...' : 'Vincular Dupla'}
                </Button>
              </form>

              <div className="p-4 bg-primary/5 rounded-lg">
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  <strong>Dica:</strong> Peça o código de convite do seu parceiro. 
                  Você encontra seu próprio código na página de Perfil.
                </p>
              </div>
            </div>
          </Card>
        )}

        {/* Info Card */}
        <Card>
          <h3 className="text-lg font-bold text-gray-900 dark:text-gray-100 mb-2">
            Sobre Duplas
          </h3>
          <ul className="space-y-2 text-sm text-gray-600 dark:text-gray-400">
            <li>• Forme uma dupla para gerenciar finanças em conjunto</li>
            <li>• Compare seus gastos e receitas com seu parceiro</li>
            <li>• Acompanhe objetivos financeiros compartilhados</li>
            <li>• Ganhe XP bônus ao completar desafios da dupla</li>
          </ul>
        </Card>
      </div>
    </Layout>
  );
};


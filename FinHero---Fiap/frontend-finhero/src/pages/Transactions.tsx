import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Layout } from '../components/common/Layout';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { Modal } from '../components/common/Modal';
import { Input } from '../components/common/Input';
import { Loading } from '../components/common/Loading';
import { transactionService } from '../services/transactions';
import { categoryService } from '../services/categories';
import { useToast } from '../context/ToastContext';
import { useGamification } from '../context/GamificationContext';
import type { Transaction, Category, TransactionType, CreateTransactionDTO } from '../types';
import { formatCurrency, formatDateTime } from '../utils/formatters';
import { validateAmount } from '../utils/validators';
import { ArrowUpCircle, ArrowDownCircle, Plus, Filter } from 'lucide-react';

export const Transactions = () => {
  const { showToast } = useToast();
  const { addXP, checkAchievements } = useGamification();
  const [loading, setLoading] = useState(true);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [filterType, setFilterType] = useState<TransactionType | 'ALL'>('ALL');

  // Form state
  const [formData, setFormData] = useState<CreateTransactionDTO>({
    type: 'DESPESA',
    amount: 0,
    description: '',
    categoryId: 0,
  });
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [transactionsResponse, categoriesData] = await Promise.all([
        transactionService.getAll(0, 100),
        categoryService.getAll(),
      ]);

      setTransactions(transactionsResponse.content);
      setCategories(categoriesData);

      // Verificar conquistas
      await checkAchievements({
        transactionCount: transactionsResponse.content.length,
      });
    } catch (error: any) {
      showToast('Erro ao carregar dados', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormErrors({});

    // Validações
    const errors: Record<string, string> = {};

    if (!formData.type) {
      errors.type = 'Tipo é obrigatório';
    }

    const amountValidation = validateAmount(formData.amount);
    if (!amountValidation.valid) {
      errors.amount = amountValidation.message || 'Valor inválido';
    }

    if (!formData.categoryId || formData.categoryId === 0) {
      errors.categoryId = 'Categoria é obrigatória';
    }

    if (Object.keys(errors).length > 0) {
      setFormErrors(errors);
      return;
    }

    try {
      await transactionService.create(formData);
      showToast('Transação criada com sucesso!', 'success');
      addXP(10, 'Criar transação');
      setIsModalOpen(false);
      setFormData({
        type: 'DESPESA',
        amount: 0,
        description: '',
        categoryId: 0,
      });
      await loadData();
    } catch (error: any) {
      const errorMessage = error.response?.data?.error || error.response?.data?.message || 'Erro ao criar transação';
      showToast(errorMessage, 'error');
    }
  };

  const filteredTransactions = transactions.filter((t) => 
    filterType === 'ALL' || t.type === filterType
  );

  if (loading) {
    return (
      <Layout>
        <Loading fullScreen />
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between flex-wrap gap-4">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100">
            Transações
          </h1>
          <Button
            variant="primary"
            onClick={() => setIsModalOpen(true)}
          >
            <Plus className="w-5 h-5 inline mr-2" />
            Nova Transação
          </Button>
        </div>

        {/* Filters */}
        <Card>
          <div className="flex items-center gap-4 flex-wrap">
            <Filter className="w-5 h-5 text-gray-600 dark:text-gray-400" />
            <div className="flex gap-2">
              <button
                onClick={() => setFilterType('ALL')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  filterType === 'ALL'
                    ? 'bg-primary text-white'
                    : 'bg-gray-200 dark:bg-slate-700 text-gray-700 dark:text-gray-300'
                }`}
              >
                Todas
              </button>
              <button
                onClick={() => setFilterType('RECEITA')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  filterType === 'RECEITA'
                    ? 'bg-success text-white'
                    : 'bg-gray-200 dark:bg-slate-700 text-gray-700 dark:text-gray-300'
                }`}
              >
                Receitas
              </button>
              <button
                onClick={() => setFilterType('DESPESA')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  filterType === 'DESPESA'
                    ? 'bg-danger text-white'
                    : 'bg-gray-200 dark:bg-slate-700 text-gray-700 dark:text-gray-300'
                }`}
              >
                Despesas
              </button>
            </div>
          </div>
        </Card>

        {/* Transactions List */}
        <Card>
          {filteredTransactions.length === 0 ? (
            <div className="text-center py-12 text-gray-500 dark:text-gray-400">
              <p className="text-lg">Nenhuma transação encontrada</p>
              <Button
                variant="primary"
                className="mt-4"
                onClick={() => setIsModalOpen(true)}
              >
                Criar primeira transação
              </Button>
            </div>
          ) : (
            <div className="space-y-3">
              {filteredTransactions.map((transaction) => (
                <motion.div
                  key={transaction.id}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="flex items-center justify-between p-4 bg-gray-50 dark:bg-slate-700 rounded-lg hover:shadow-md transition-shadow"
                >
                  <div className="flex items-center gap-4">
                    {transaction.type === 'RECEITA' ? (
                      <ArrowUpCircle className="w-8 h-8 text-success" />
                    ) : (
                      <ArrowDownCircle className="w-8 h-8 text-danger" />
                    )}
                    <div>
                      <p className="font-medium text-gray-900 dark:text-gray-100">
                        {transaction.description || 'Sem descrição'}
                      </p>
                      <p className="text-sm text-gray-600 dark:text-gray-400">
                        {formatDateTime(transaction.createdAt)}
                      </p>
                      <p className="text-xs text-gray-500 dark:text-gray-500 mt-1">
                        Categoria ID: {transaction.categoryId}
                      </p>
                    </div>
                  </div>
                  <p
                    className={`text-xl font-bold ${
                      transaction.type === 'RECEITA' ? 'text-success' : 'text-danger'
                    }`}
                  >
                    {transaction.type === 'RECEITA' ? '+' : '-'}
                    {formatCurrency(Number(transaction.amount))}
                  </p>
                </motion.div>
              ))}
            </div>
          )}
        </Card>
      </div>

      {/* Modal de Nova Transação */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setFormData({
            type: 'DESPESA',
            amount: 0,
            description: '',
            categoryId: 0,
          });
          setFormErrors({});
        }}
        title="Nova Transação"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Tipo
            </label>
            <select
              value={formData.type}
              onChange={(e) =>
                setFormData({ ...formData, type: e.target.value as TransactionType })
              }
              className="input"
            >
              <option value="RECEITA">Receita</option>
              <option value="DESPESA">Despesa</option>
            </select>
            {formErrors.type && (
              <p className="mt-1 text-sm text-danger">{formErrors.type}</p>
            )}
          </div>

          <Input
            type="number"
            label="Valor"
            step="0.01"
            min="0.01"
            value={formData.amount || ''}
            onChange={(e) =>
              setFormData({ ...formData, amount: parseFloat(e.target.value) || 0 })
            }
            error={formErrors.amount}
            placeholder="0.00"
          />

          <Input
            label="Descrição"
            value={formData.description}
            onChange={(e) =>
              setFormData({ ...formData, description: e.target.value })
            }
            placeholder="Descrição da transação (opcional)"
          />

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Categoria
            </label>
            <select
              value={formData.categoryId}
              onChange={(e) =>
                setFormData({ ...formData, categoryId: parseInt(e.target.value) })
              }
              className="input"
            >
              <option value={0}>Selecione uma categoria</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {cat.name}
                </option>
              ))}
            </select>
            {formErrors.categoryId && (
              <p className="mt-1 text-sm text-danger">{formErrors.categoryId}</p>
            )}
          </div>

          <div className="flex gap-3 pt-4">
            <Button type="submit" variant="primary" fullWidth>
              Criar Transação
            </Button>
            <Button
              type="button"
              variant="secondary"
              fullWidth
              onClick={() => setIsModalOpen(false)}
            >
              Cancelar
            </Button>
          </div>
        </form>
      </Modal>
    </Layout>
  );
};


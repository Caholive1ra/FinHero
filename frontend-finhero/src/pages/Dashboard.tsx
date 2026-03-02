import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { Layout } from '../components/common/Layout';
import { Card } from '../components/common/Card';
import { Loading } from '../components/common/Loading';
import { transactionService } from '../services/transactions';
import { useToast } from '../context/ToastContext';
import { useGamification } from '../context/GamificationContext';
import type { Transaction } from '../types';
import { formatCurrency, formatDate } from '../utils/formatters';
import { ArrowUpCircle, ArrowDownCircle, TrendingUp, Plus } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

export const Dashboard = () => {
  const { showToast } = useToast();
  const { checkAchievements } = useGamification();
  const [loading, setLoading] = useState(true);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [summary, setSummary] = useState({
    receitas: 0,
    despesas: 0,
    saldo: 0,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const response = await transactionService.getAll(0, 100);
      const allTransactions = response.content;
      setTransactions(allTransactions.slice(0, 10)); // Últimas 10 para exibir

      // Calcular resumo
      const receitas = allTransactions
        .filter((t) => t.type === 'RECEITA')
        .reduce((sum, t) => sum + Number(t.amount), 0);
      const despesas = allTransactions
        .filter((t) => t.type === 'DESPESA')
        .reduce((sum, t) => sum + Number(t.amount), 0);

      setSummary({
        receitas,
        despesas,
        saldo: receitas - despesas,
      });

      // Verificar conquistas
      await checkAchievements({
        transactionCount: allTransactions.length,
      });
    } catch (error: any) {
      showToast('Erro ao carregar dados', 'error');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Layout>
        <Loading fullScreen />
      </Layout>
    );
  }

  // Preparar dados para gráficos
  const chartData = transactions.slice(-7).reverse().map((t) => ({
    date: formatDate(t.createdAt),
    receita: t.type === 'RECEITA' ? Number(t.amount) : 0,
    despesa: t.type === 'DESPESA' ? Number(t.amount) : 0,
  }));

  const pieData = [
    { name: 'Receitas', value: summary.receitas },
    { name: 'Despesas', value: summary.despesas },
  ];

  const COLORS = ['#10B981', '#EF4444'];

  return (
    <Layout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100">
            Dashboard
          </h1>
          <Link to="/transactions">
            <motion.button
              className="btn-primary flex items-center gap-2"
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <Plus className="w-5 h-5" />
              Nova Transação
            </motion.button>
          </Link>
        </div>

        {/* Summary Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                  Receitas
                </p>
                <p className="text-2xl font-bold text-success">
                  {formatCurrency(summary.receitas)}
                </p>
              </div>
              <ArrowUpCircle className="w-12 h-12 text-success" />
            </div>
          </Card>

          <Card>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                  Despesas
                </p>
                <p className="text-2xl font-bold text-danger">
                  {formatCurrency(summary.despesas)}
                </p>
              </div>
              <ArrowDownCircle className="w-12 h-12 text-danger" />
            </div>
          </Card>

          <Card>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                  Saldo
                </p>
                <p className={`text-2xl font-bold ${summary.saldo >= 0 ? 'text-success' : 'text-danger'}`}>
                  {formatCurrency(summary.saldo)}
                </p>
              </div>
              <TrendingUp className="w-12 h-12 text-primary" />
            </div>
          </Card>
        </div>

        {/* Charts */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card>
            <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">
              Últimos 7 Dias
            </h2>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Line
                  type="monotone"
                  dataKey="receita"
                  stroke="#10B981"
                  strokeWidth={2}
                  name="Receitas"
                />
                <Line
                  type="monotone"
                  dataKey="despesa"
                  stroke="#EF4444"
                  strokeWidth={2}
                  name="Despesas"
                />
              </LineChart>
            </ResponsiveContainer>
          </Card>

          <Card>
            <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">
              Visão Geral
            </h2>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={(props: any) => `${props.name}: ${(props.percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {pieData.map((_entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </Card>
        </div>

        {/* Recent Transactions */}
        <Card>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100">
              Transações Recentes
            </h2>
            <Link
              to="/transactions"
              className="text-primary hover:text-primary-dark text-sm font-medium"
            >
              Ver todas
            </Link>
          </div>

          {transactions.length === 0 ? (
            <div className="text-center py-8 text-gray-500 dark:text-gray-400">
              <p>Nenhuma transação ainda</p>
              <Link to="/transactions" className="text-primary hover:underline mt-2 inline-block">
                Criar primeira transação
              </Link>
            </div>
          ) : (
            <div className="space-y-2">
              {transactions.map((transaction) => (
                <motion.div
                  key={transaction.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  className="flex items-center justify-between p-4 bg-gray-50 dark:bg-slate-700 rounded-lg"
                >
                  <div className="flex items-center gap-3">
                    {transaction.type === 'RECEITA' ? (
                      <ArrowUpCircle className="w-6 h-6 text-success" />
                    ) : (
                      <ArrowDownCircle className="w-6 h-6 text-danger" />
                    )}
                    <div>
                      <p className="font-medium text-gray-900 dark:text-gray-100">
                        {transaction.description || 'Sem descrição'}
                      </p>
                      <p className="text-sm text-gray-600 dark:text-gray-400">
                        {formatDate(transaction.createdAt)}
                      </p>
                    </div>
                  </div>
                  <p
                    className={`font-bold ${
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
    </Layout>
  );
};


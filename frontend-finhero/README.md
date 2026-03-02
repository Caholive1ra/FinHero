# FinHero Frontend

Frontend React gamificado para o FinHero - Aplicação de gestão financeira colaborativa.

## 🚀 Tecnologias

- **React 19** com TypeScript
- **Vite** - Build tool
- **Tailwind CSS** - Estilização
- **Framer Motion** - Animações
- **React Router** - Roteamento
- **Axios** - Cliente HTTP
- **Recharts** - Gráficos
- **React Hook Form** - Formulários
- **Zod** - Validação

## 📋 Pré-requisitos

- Node.js >= 18.0.0
- npm >= 9.0.0
- Backend FinHero rodando em `http://localhost:8080`

## 🔧 Instalação

```bash
# Instalar dependências
npm install

# Criar arquivo .env
cp .env.example .env

# Editar .env e configurar a URL da API se necessário
# VITE_API_URL=http://localhost:8080/api
```

## 🏃 Execução

```bash
# Modo desenvolvimento
npm run dev

# Build para produção
npm run build

# Preview da build
npm run preview

# Lint
npm run lint
```

## 📁 Estrutura do Projeto

```
src/
├── components/          # Componentes reutilizáveis
│   ├── common/         # Componentes básicos (Button, Input, Card, etc.)
│   ├── gamification/   # Componentes de gamificação (XPBar, AchievementCard)
│   ├── dashboard/      # Componentes do dashboard
│   └── transactions/   # Componentes de transações
├── pages/              # Páginas da aplicação
│   ├── Login.tsx
│   ├── Register.tsx
│   ├── Dashboard.tsx
│   ├── Transactions.tsx
│   ├── Profile.tsx
│   └── Dupla.tsx
├── services/           # Serviços de API
│   ├── api.ts
│   ├── auth.ts
│   ├── transactions.ts
│   ├── categories.ts
│   └── dupla.ts
├── context/            # Context API
│   ├── AuthContext.tsx
│   ├── ThemeContext.tsx
│   └── GamificationContext.tsx
├── hooks/              # Custom hooks
│   └── useToast.ts
├── utils/              # Utilitários
│   ├── constants.ts
│   ├── formatters.ts
│   ├── validators.ts
│   └── storage.ts
└── types/              # TypeScript types
    └── index.ts
```

## 🎮 Funcionalidades

### Implementadas
- ✅ Autenticação (Login/Registro)
- ✅ Dashboard com gráficos e resumo financeiro
- ✅ Gestão de transações (criar, listar, filtrar)
- ✅ Sistema de duplas (vincular via código convite)
- ✅ Perfil do usuário com estatísticas
- ✅ Sistema de gamificação (XP, níveis, conquistas)
- ✅ Modo dark/light com persistência
- ✅ Design responsivo e moderno
- ✅ Animações suaves com Framer Motion

### APIs Integradas
- `POST /api/auth/register` - Cadastro
- `POST /api/auth/login` - Login
- `GET /api/users/me` - Usuário atual
- `POST /api/transactions` - Criar transação
- `GET /api/transactions` - Listar transações
- `POST /api/dupla/link` - Vincular dupla
- `GET /api/categories` - Listar categorias

## 🎨 Design

O frontend foi desenvolvido com foco em:
- **Gamificação**: Sistema de XP, níveis e conquistas
- **Dark Mode**: Tema escuro com transições suaves
- **Animações**: Transições e feedback visual em todas as ações
- **Responsividade**: Mobile-first, funciona em todos os dispositivos
- **Acessibilidade**: Navegação por teclado e contraste adequado

## 🔐 Autenticação

O token JWT é armazenado no `localStorage` e automaticamente incluído em todas as requisições através dos interceptors do Axios.

## 🎯 Gamificação

O sistema de gamificação é totalmente frontend:
- **XP**: Ganha pontos ao realizar ações
- **Níveis**: Calculados baseados no XP total
- **Conquistas**: Desbloqueáveis por ações específicas
- Tudo é persistido no `localStorage`

## 🌐 Variáveis de Ambiente

```env
VITE_API_URL=http://localhost:8080/api
```

## 📝 Notas

- O backend deve estar rodando antes de iniciar o frontend
- CORS está configurado no backend para aceitar requisições do frontend
- A gamificação é apenas visual - não há endpoints específicos no backend

## 🐛 Troubleshooting

### Erro de CORS
Verifique se o backend está rodando e se o CORS está configurado corretamente.

### Token inválido
Faça logout e login novamente. O token pode ter expirado.

### Dados não carregam
Verifique se a URL da API está correta no arquivo `.env`.

## 📚 Scripts Disponíveis

- `npm run dev` - Inicia servidor de desenvolvimento
- `npm run build` - Cria build de produção
- `npm run preview` - Preview da build de produção
- `npm run lint` - Executa o linter

## 🎉 Pronto para Usar!

O frontend está completamente funcional e integrado com o backend. Apenas certifique-se de que o backend está rodando e configure a URL da API no arquivo `.env`.

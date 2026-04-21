import { useState } from 'react'
import { motion } from 'framer-motion'
import { Eye, EyeOff, ArrowRight, RotateCw, Sparkles } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../components/Toast'
import { api } from '../api/client'

export default function AuthPage() {
  const { login } = useAuth()
  const toast = useToast()
  const [mode, setMode] = useState('login')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [showPw, setShowPw] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      if (mode === 'register') {
        await api.auth.register(username, password)
        toast('Welcome. Signing you in…', 'success')
      }
      const res = await api.auth.login(username, password)
      login(res.data.accessToken, username)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-ink-950 flex">
      {/* Left — brand pane */}
      <div className="hidden lg:flex flex-col justify-between w-1/2 relative overflow-hidden p-12 border-r border-ink-800">
        <div className="absolute inset-0 bg-grid opacity-60" />
        <div className="absolute inset-0 bg-radial-glow" />

        {/* Decorative orbit */}
        <div className="absolute -right-32 top-1/2 -translate-y-1/2 pointer-events-none">
          <motion.div
            animate={{ rotate: 360 }}
            transition={{ duration: 80, ease: 'linear', repeat: Infinity }}
            className="w-[600px] h-[600px] rounded-full border border-accent-500/10"
          >
            <div className="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-1/2 w-3 h-3 rounded-full bg-accent-500 shadow-glow-accent" />
          </motion.div>
          <motion.div
            animate={{ rotate: -360 }}
            transition={{ duration: 120, ease: 'linear', repeat: Infinity }}
            className="absolute inset-12 rounded-full border border-accent-500/15"
          />
          <motion.div
            animate={{ rotate: 360 }}
            transition={{ duration: 40, ease: 'linear', repeat: Infinity }}
            className="absolute inset-28 rounded-full border border-accent-500/25"
          >
            <div className="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-1/2 w-2 h-2 rounded-full bg-accent-400" />
          </motion.div>
        </div>

        <div className="relative">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-ink-900 border border-ink-800 flex items-center justify-center">
              <RotateCw className="w-4 h-4 text-accent-400" />
            </div>
            <span className="font-display text-2xl italic text-white">Revolve</span>
          </div>
        </div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1, duration: 0.6 }}
          className="relative max-w-md"
        >
          <h1 className="font-display text-5xl leading-[1.1] text-white text-balance">
            Your week,<br />
            <span className="italic text-accent-300">on repeat.</span>
          </h1>
          <p className="mt-5 text-ink-400 text-base leading-relaxed text-balance">
            A minimalist academic tracker. Set your recurring activities once, and let each week
            revolve into view. Flip cards as you go. Don't lose sight of what's left undone.
          </p>

          <div className="mt-10 flex items-center gap-6 text-xs text-ink-500">
            <Feature label="Weekly rhythm" />
            <Feature label="Card flips" />
            <Feature label="Auto backlog" />
          </div>
        </motion.div>

        <p className="relative text-xs text-ink-600">
          Built for students. Open source.
        </p>
      </div>

      {/* Right — form pane */}
      <div className="flex-1 flex items-center justify-center p-6 lg:p-12">
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="w-full max-w-sm"
        >
          {/* Mobile brand */}
          <div className="lg:hidden flex items-center gap-2.5 mb-10 justify-center">
            <div className="w-8 h-8 rounded-lg bg-ink-900 border border-ink-800 flex items-center justify-center">
              <RotateCw className="w-4 h-4 text-accent-400" />
            </div>
            <span className="font-display text-2xl italic text-white">Revolve</span>
          </div>

          <h2 className="text-xl font-semibold text-white">
            {mode === 'login' ? 'Welcome back' : 'Create your account'}
          </h2>
          <p className="text-sm text-ink-400 mt-1">
            {mode === 'login' ? 'Sign in to continue your week.' : 'A few seconds, and you\'re in.'}
          </p>

          <div className="mt-8 flex p-1 bg-ink-900 border border-ink-800 rounded-lg">
            <TabButton active={mode === 'login'} onClick={() => { setMode('login'); setError('') }}>
              Sign in
            </TabButton>
            <TabButton active={mode === 'register'} onClick={() => { setMode('register'); setError('') }}>
              Register
            </TabButton>
          </div>

          <form onSubmit={handleSubmit} className="mt-6 space-y-4">
            <div>
              <label className="label">Username</label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                autoComplete="username"
                className="input-field"
                placeholder="jane_doe"
              />
            </div>
            <div>
              <label className="label">Password</label>
              <div className="relative">
                <input
                  type={showPw ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
                  className="input-field pr-10"
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  onClick={() => setShowPw((v) => !v)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-ink-500 hover:text-ink-300 transition-colors"
                  aria-label={showPw ? 'Hide password' : 'Show password'}
                >
                  {showPw ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {error && (
              <motion.p
                initial={{ opacity: 0, y: -4 }}
                animate={{ opacity: 1, y: 0 }}
                className="text-rose-400 text-sm"
              >
                {error}
              </motion.p>
            )}

            <button type="submit" disabled={loading} className="btn-primary w-full !py-2.5 mt-1">
              {loading ? (
                <span className="inline-block w-4 h-4 border-2 border-ink-700 border-t-ink-950 rounded-full animate-spin" />
              ) : (
                <>
                  {mode === 'login' ? 'Sign in' : 'Create account'}
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </form>

          <p className="mt-6 text-xs text-ink-600 text-center flex items-center gap-1.5 justify-center">
            <Sparkles className="w-3 h-3 text-accent-400" />
            Free, for students, forever.
          </p>
        </motion.div>
      </div>
    </div>
  )
}

function TabButton({ active, onClick, children }) {
  return (
    <button
      onClick={onClick}
      className={`relative flex-1 py-1.5 text-sm font-medium rounded-md transition-colors ${
        active ? 'text-white' : 'text-ink-400 hover:text-ink-200'
      }`}
    >
      {active && (
        <motion.span
          layoutId="auth-tab"
          className="absolute inset-0 bg-ink-800 rounded-md border border-ink-700"
          transition={{ type: 'spring', stiffness: 400, damping: 32 }}
        />
      )}
      <span className="relative">{children}</span>
    </button>
  )
}

function Feature({ label }) {
  return (
    <div className="flex items-center gap-2">
      <span className="w-1 h-1 rounded-full bg-accent-400" />
      <span className="uppercase tracking-[0.12em] text-[10px] text-ink-400">{label}</span>
    </div>
  )
}

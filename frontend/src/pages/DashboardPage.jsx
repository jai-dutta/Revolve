import { useState, useEffect, useCallback, useRef } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import {
  RotateCw,
  ChevronLeft,
  ChevronRight,
  Settings,
  LogOut,
  Plus,
  CalendarDays,
  Loader2,
  Clock3,
  CircleCheck,
  ListChecks,
  Sparkles,
} from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../components/Toast'
import { api } from '../api/client'
import WeeklyCalendar from '../components/WeeklyCalendar'
import BacklogSection from '../components/BacklogSection'
import ManageActivitiesModal from '../components/ManageActivitiesModal'

function getMondayOfWeek(date) {
  const d = new Date(date)
  const day = d.getDay()
  const diff = day === 0 ? -6 : 1 - day
  d.setDate(d.getDate() + diff)
  d.setHours(0, 0, 0, 0)
  return d
}

function toISODate(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

function formatWeekLabel(monday) {
  const sunday = new Date(monday)
  sunday.setDate(sunday.getDate() + 6)
  const sameMonth = monday.getMonth() === sunday.getMonth()
  const opts = { day: 'numeric', month: sameMonth ? undefined : 'short' }
  return `${monday.toLocaleDateString('en-GB', opts)} – ${sunday.toLocaleDateString('en-GB', { day: 'numeric', month: 'short' })}`
}

function weekOffset(monday) {
  const current = getMondayOfWeek(new Date())
  return Math.round((monday - current) / (7 * 24 * 60 * 60 * 1000))
}

function relativeWeekLabel(offset) {
  if (offset === 0) return 'This week'
  if (offset === -1) return 'Last week'
  if (offset === 1) return 'Next week'
  if (offset < 0) return `${-offset} weeks ago`
  return `In ${offset} weeks`
}

function formatHours(minutes) {
  if (!minutes) return '0h'
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return [h ? `${h}h` : null, m ? `${m}m` : null].filter(Boolean).join(' ')
}

export default function DashboardPage() {
  const { username, logout } = useAuth()
  const toast = useToast()
  const [weekMonday, setWeekMonday] = useState(() => getMondayOfWeek(new Date()))
  const [instances, setInstances] = useState([])
  const [backlog, setBacklog] = useState([])
  const [loadingWeek, setLoadingWeek] = useState(true)
  const [showManage, setShowManage] = useState(false)
  const menuRef = useRef(null)
  const [menuOpen, setMenuOpen] = useState(false)

  const fetchWeek = useCallback(async (monday) => {
    setLoadingWeek(true)
    try {
      const res = await api.instances.getWeek(toISODate(monday))
      setInstances(res?.data || [])
    } catch (err) {
      toast(err.message, 'error')
    } finally {
      setLoadingWeek(false)
    }
  }, [toast])

  const fetchBacklog = useCallback(async () => {
    try {
      const res = await api.instances.backlog()
      setBacklog(res?.data || [])
    } catch {/* non-critical */}
  }, [])

  useEffect(() => { fetchWeek(weekMonday); fetchBacklog() }, [weekMonday, fetchWeek, fetchBacklog])

  useEffect(() => {
    function onKey(e) {
      if (showManage) return
      if (e.key === 'ArrowLeft' && e.metaKey) goToPrevWeek()
      if (e.key === 'ArrowRight' && e.metaKey) goToNextWeek()
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [showManage])

  // Close menu on outside click
  useEffect(() => {
    function onClick(e) { if (menuRef.current && !menuRef.current.contains(e.target)) setMenuOpen(false) }
    if (menuOpen) document.addEventListener('mousedown', onClick)
    return () => document.removeEventListener('mousedown', onClick)
  }, [menuOpen])

  async function handleToggle(id) {
    const prevInstances = instances
    const prevBacklog = backlog
    // optimistic update
    setInstances((p) => p.map((i) => i.id === id ? { ...i, completed: !i.completed } : i))
    setBacklog((b) => b.map((i) => i.id === id ? { ...i, completed: !i.completed } : i))
    try {
      const res = await api.instances.toggleComplete(id)
      const updated = res.data
      setInstances((p) => p.map((i) => i.id === id ? updated : i))
      setBacklog((b) => b.map((i) => i.id === id ? updated : i))
      if (updated.completed) toast(`Done: ${updated.activityName}`, 'success')
    } catch (err) {
      setInstances(prevInstances)
      setBacklog(prevBacklog)
      toast(err.message, 'error')
    }
  }

  function goToPrevWeek() { setWeekMonday((p) => { const d = new Date(p); d.setDate(d.getDate() - 7); return d }) }
  function goToNextWeek() { setWeekMonday((p) => { const d = new Date(p); d.setDate(d.getDate() + 7); return d }) }
  function goToCurrentWeek() { setWeekMonday(getMondayOfWeek(new Date())) }

  const isCurrentWeek = toISODate(weekMonday) === toISODate(getMondayOfWeek(new Date()))
  const completedCount = instances.filter((i) => i.completed).length
  const totalCount = instances.length
  const progressPct = totalCount > 0 ? (completedCount / totalCount) * 100 : 0
  const scheduledMinutes = instances.reduce((sum, i) => sum + (i.durationMinutes || 0), 0)
  const completedMinutes = instances.reduce((sum, i) => sum + (i.completed ? i.durationMinutes || 0 : 0), 0)
  const allDone = totalCount > 0 && completedCount === totalCount

  return (
    <div className="min-h-screen bg-ink-950">
      {/* Header */}
      <header className="sticky top-0 z-30 backdrop-blur-xl bg-ink-950/70 border-b border-ink-800/80">
        <div className="max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8 py-3.5 flex items-center gap-3 sm:gap-6">
          {/* Logo */}
          <div className="flex items-center gap-2">
            <div className="w-7 h-7 rounded-lg bg-ink-900 border border-ink-800 flex items-center justify-center">
              <RotateCw className="w-3.5 h-3.5 text-accent-400" />
            </div>
            <span className="font-display text-lg italic text-white hidden sm:inline">Revolve</span>
          </div>

          {/* Week nav */}
          <div className="flex items-center gap-1 ml-auto sm:ml-4">
            <button onClick={goToPrevWeek} className="btn-ghost !p-1.5" aria-label="Previous week">
              <ChevronLeft className="w-4 h-4" />
            </button>
            <button
              onClick={goToCurrentWeek}
              className={`text-sm px-3 py-1.5 rounded-lg transition-colors tabular-nums ${
                isCurrentWeek ? 'text-white bg-ink-900 border border-ink-800' : 'text-ink-400 hover:text-white hover:bg-ink-900'
              }`}
            >
              <span className="flex items-center gap-2">
                <CalendarDays className="w-3.5 h-3.5" />
                {formatWeekLabel(weekMonday)}
              </span>
            </button>
            <button onClick={goToNextWeek} className="btn-ghost !p-1.5" aria-label="Next week">
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>

          {/* Progress — desktop */}
          {totalCount > 0 && (
            <div className="hidden md:flex items-center gap-3 ml-auto">
              <div className="flex items-center gap-2">
                <div className="w-28 h-1.5 bg-ink-800 rounded-full overflow-hidden">
                  <motion.div
                    className="h-full bg-gradient-to-r from-accent-500 to-accent-400"
                    initial={{ width: 0 }}
                    animate={{ width: `${progressPct}%` }}
                    transition={{ type: 'spring', stiffness: 200, damping: 28 }}
                  />
                </div>
                <span className="text-[11px] text-ink-500 tabular-nums">
                  {completedCount}/{totalCount}
                </span>
              </div>
            </div>
          )}

          {/* Actions */}
          <div className="flex items-center gap-1.5 md:ml-0 ml-auto">
            <button onClick={() => setShowManage(true)} className="btn-outline !px-2.5 sm:!px-3 !py-1.5">
              <Settings className="w-3.5 h-3.5" />
              <span className="hidden sm:inline">Manage</span>
            </button>

            <div ref={menuRef} className="relative">
              <button
                onClick={() => setMenuOpen((v) => !v)}
                className="w-8 h-8 rounded-full bg-ink-900 border border-ink-800 hover:border-ink-700 flex items-center justify-center text-xs font-medium text-ink-300 transition-colors"
              >
                {username?.[0]?.toUpperCase() || 'U'}
              </button>
              <AnimatePresence>
                {menuOpen && (
                  <motion.div
                    initial={{ opacity: 0, y: -4, scale: 0.97 }}
                    animate={{ opacity: 1, y: 0, scale: 1 }}
                    exit={{ opacity: 0, scale: 0.98 }}
                    transition={{ duration: 0.15 }}
                    className="absolute right-0 mt-2 w-52 card-surface shadow-soft p-1 origin-top-right"
                  >
                    <div className="px-3 py-2 border-b border-ink-800 mb-1">
                      <p className="text-xs text-ink-500">Signed in as</p>
                      <p className="text-sm text-white font-medium truncate">{username}</p>
                    </div>
                    <button
                      onClick={() => { logout(); toast('Signed out', 'info') }}
                      className="w-full flex items-center gap-2 px-3 py-2 text-sm text-ink-300 hover:text-white hover:bg-ink-850 rounded-md transition-colors"
                    >
                      <LogOut className="w-3.5 h-3.5" />
                      Sign out
                    </button>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          </div>
        </div>

        {/* Mobile progress bar */}
        {totalCount > 0 && (
          <div className="md:hidden h-0.5 bg-ink-900">
            <motion.div
              className="h-full bg-gradient-to-r from-accent-500 to-accent-400"
              initial={{ width: 0 }}
              animate={{ width: `${progressPct}%` }}
              transition={{ type: 'spring', stiffness: 200, damping: 28 }}
            />
          </div>
        )}
      </header>

      {/* Main */}
      <main className="max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-10">
        {loadingWeek && instances.length === 0 ? (
          <div className="flex items-center justify-center py-32">
            <Loader2 className="w-6 h-6 text-ink-600 animate-spin" />
          </div>
        ) : instances.length === 0 ? (
          <EmptyState onAdd={() => setShowManage(true)} />
        ) : (
          <>
            <WeekSummary
              offsetLabel={relativeWeekLabel(weekOffset(weekMonday))}
              rangeLabel={formatWeekLabel(weekMonday)}
              scheduledMinutes={scheduledMinutes}
              completedMinutes={completedMinutes}
              remaining={totalCount - completedCount}
              allDone={allDone}
            />
            <WeeklyCalendar instances={instances} weekStartDate={weekMonday} onToggle={handleToggle} />
            <BacklogSection backlog={backlog} onToggle={handleToggle} />
          </>
        )}
      </main>

      <AnimatePresence>
        {showManage && (
          <ManageActivitiesModal
            onClose={() => setShowManage(false)}
            onSaved={() => fetchWeek(weekMonday)}
          />
        )}
      </AnimatePresence>
    </div>
  )
}

function WeekSummary({ offsetLabel, rangeLabel, scheduledMinutes, completedMinutes, remaining, allDone }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      className="mb-8 flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4"
    >
      <div>
        <p className="text-[11px] uppercase tracking-[0.14em] text-ink-500">{offsetLabel}</p>
        <h1 className="mt-1 font-display text-3xl sm:text-4xl italic text-white leading-none">
          {rangeLabel}
        </h1>
      </div>

      <div className="flex flex-wrap items-center gap-2">
        <StatChip icon={Clock3} label="Scheduled" value={formatHours(scheduledMinutes)} />
        <StatChip
          icon={CircleCheck}
          label="Completed"
          value={formatHours(completedMinutes)}
          tone="emerald"
        />
        {allDone ? (
          <span className="inline-flex items-center gap-1.5 px-3 py-2 rounded-xl bg-emerald-500/10 border border-emerald-500/25 text-emerald-300 text-xs font-medium">
            <Sparkles className="w-3.5 h-3.5" />
            All caught up
          </span>
        ) : (
          <StatChip icon={ListChecks} label="Remaining" value={String(remaining)} tone="accent" />
        )}
      </div>
    </motion.div>
  )
}

function StatChip({ icon: Icon, label, value, tone = 'default' }) {
  const tones = {
    default: 'text-ink-300',
    emerald: 'text-emerald-400',
    accent: 'text-accent-400',
  }
  return (
    <div className="flex items-center gap-2.5 px-3 py-2 rounded-xl bg-ink-900 border border-ink-800">
      <Icon className={`w-4 h-4 ${tones[tone]}`} />
      <div className="flex flex-col leading-tight">
        <span className="text-[10px] uppercase tracking-[0.1em] text-ink-500">{label}</span>
        <span className="text-sm font-semibold text-white tabular-nums">{value}</span>
      </div>
    </div>
  )
}

function EmptyState({ onAdd }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className="flex flex-col items-center justify-center py-24 text-center"
    >
      <div className="relative">
        <div className="w-20 h-20 rounded-full bg-ink-900 border border-ink-800 flex items-center justify-center">
          <CalendarDays className="w-8 h-8 text-ink-600" />
        </div>
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 30, ease: 'linear', repeat: Infinity }}
          className="absolute inset-0 rounded-full border border-accent-500/20"
        />
      </div>
      <h2 className="mt-6 font-display text-2xl italic text-white">Your week is a blank slate.</h2>
      <p className="mt-2 text-ink-500 max-w-sm">
        Add your first recurring activity — a lecture, a workshop, a study block — and it'll appear here every week.
      </p>
      <button onClick={onAdd} className="btn-primary mt-6">
        <Plus className="w-4 h-4" />
        Add your first activity
      </button>
    </motion.div>
  )
}

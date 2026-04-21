import { useState } from 'react'
import { motion } from 'framer-motion'
import { Check, Clock, Flame, GraduationCap, BookOpen, FlaskConical, Users, Wrench, BookMarked } from 'lucide-react'

// Harmonious, muted-but-vivid accents with icon pairing
const TYPE_STYLES = {
  LECTURE:          { icon: GraduationCap, tint: 'from-indigo-500/15  to-indigo-400/5', ring: 'ring-indigo-500/30',  dot: 'bg-indigo-400',  label: 'Lecture' },
  WORKSHOP:         { icon: Wrench,        tint: 'from-emerald-500/15 to-emerald-400/5', ring: 'ring-emerald-500/30', dot: 'bg-emerald-400', label: 'Workshop' },
  TUTORIAL:         { icon: Users,         tint: 'from-violet-500/15  to-violet-400/5',  ring: 'ring-violet-500/30',  dot: 'bg-violet-400',  label: 'Tutorial' },
  LABORATORY:       { icon: FlaskConical,  tint: 'from-amber-500/15   to-amber-400/5',   ring: 'ring-amber-500/30',   dot: 'bg-amber-400',   label: 'Lab' },
  SEMINAR:          { icon: BookOpen,      tint: 'from-rose-500/15    to-rose-400/5',    ring: 'ring-rose-500/30',    dot: 'bg-rose-400',    label: 'Seminar' },
  INDEPENDENT_STUDY:{ icon: BookMarked,    tint: 'from-sky-500/15     to-sky-400/5',     ring: 'ring-sky-500/30',     dot: 'bg-sky-400',     label: 'Study' },
}

function formatTime(timeStr) {
  if (!timeStr) return ''
  const [h, m] = timeStr.split(':')
  const hour = parseInt(h, 10)
  const ampm = hour >= 12 ? 'pm' : 'am'
  const display = hour % 12 || 12
  return `${display}:${m}${ampm}`
}

export default function ActivityCard({ instance, onToggle, compact = false }) {
  const [busy, setBusy] = useState(false)
  const style = TYPE_STYLES[instance.activityType] || TYPE_STYLES.LECTURE
  const Icon = style.icon
  const isCompleted = instance.completed
  const isBacklogged = instance.backlogged && !isCompleted

  async function handleFlip() {
    if (busy) return
    setBusy(true)
    try { await onToggle(instance.id) } finally { setBusy(false) }
  }

  return (
    <div className="perspective-1000 w-full">
      <motion.div
        animate={{ rotateY: isCompleted ? 180 : 0 }}
        transition={{ type: 'spring', stiffness: 260, damping: 24 }}
        className="relative w-full transform-style-3d cursor-pointer"
        style={{ transformStyle: 'preserve-3d', minHeight: compact ? 92 : 108 }}
        onClick={handleFlip}
      >
        {/* Front */}
        <div
          className={`absolute inset-0 backface-hidden rounded-xl bg-ink-900 border border-ink-800 hover:border-ink-700 transition-colors overflow-hidden group`}
          style={{ backfaceVisibility: 'hidden' }}
        >
          {/* Tinted overlay */}
          <div className={`absolute inset-0 bg-gradient-to-br ${style.tint} pointer-events-none`} />
          {/* Left bar */}
          <div className={`absolute left-0 top-0 bottom-0 w-0.5 ${style.dot}`} />

          <div className="relative p-3 flex flex-col h-full gap-1.5">
            <div className="flex items-start gap-2">
              <div className={`shrink-0 w-6 h-6 rounded-md bg-ink-850 flex items-center justify-center ring-1 ${style.ring}`}>
                <Icon className="w-3 h-3 text-ink-200" />
              </div>
              <div className="min-w-0 flex-1">
                <p className="text-[11px] font-medium text-ink-500 truncate uppercase tracking-[0.06em]">
                  {instance.courseName}
                </p>
                <p className="text-sm font-semibold text-white leading-snug truncate">
                  {instance.activityName}
                </p>
              </div>
              {isBacklogged && (
                <Flame className="w-3.5 h-3.5 text-accent-400 shrink-0" title="In backlog" />
              )}
            </div>

            <div className="mt-auto flex items-center gap-1.5 text-[11px] text-ink-500">
              <Clock className="w-3 h-3" />
              <span>{formatTime(instance.startTime)}</span>
              <span className="text-ink-700">·</span>
              <span>{instance.durationMinutes}m</span>
            </div>
          </div>
        </div>

        {/* Back (completed) */}
        <div
          className="absolute inset-0 backface-hidden rounded-xl bg-ink-900/60 border border-ink-800 rotate-y-180 overflow-hidden"
          style={{ backfaceVisibility: 'hidden' }}
        >
          <div className="absolute inset-0 bg-gradient-to-br from-emerald-500/10 to-transparent" />
          <div className="relative h-full flex flex-col items-center justify-center p-2 gap-1.5">
            <div className="w-7 h-7 rounded-full bg-emerald-500/15 border border-emerald-500/30 flex items-center justify-center">
              <Check className="w-4 h-4 text-emerald-400" strokeWidth={3} />
            </div>
            <p className="text-[11px] font-medium text-ink-500 truncate max-w-full px-2">
              {instance.activityName}
            </p>
          </div>
        </div>
      </motion.div>
    </div>
  )
}

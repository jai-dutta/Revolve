import { motion } from 'framer-motion'
import { Flame } from 'lucide-react'
import ActivityCard from './ActivityCard'

export default function BacklogSection({ backlog, onToggle }) {
  const open = backlog.filter((i) => !i.completed)
  if (open.length === 0) return null

  const totalMinutes = open.reduce((sum, inst) => sum + inst.durationMinutes, 0)
  const hours = Math.floor(totalMinutes / 60)
  const mins = totalMinutes % 60

  return (
    <motion.section
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 0.15 }}
      className="mt-12"
    >
      <div className="flex items-center gap-3 mb-5">
        <div className="flex items-center gap-2.5">
          <div className="w-7 h-7 rounded-lg bg-accent-500/10 border border-accent-500/20 flex items-center justify-center">
            <Flame className="w-3.5 h-3.5 text-accent-400" />
          </div>
          <h2 className="text-sm font-semibold text-white">Backlog</h2>
        </div>

        <div className="h-px flex-1 bg-gradient-to-r from-accent-500/20 via-ink-800 to-transparent" />

        <div className="flex items-center gap-3 text-xs">
          <span className="px-2 py-0.5 rounded-md bg-ink-900 border border-ink-800 text-ink-400 tabular-nums">
            {open.length} item{open.length !== 1 ? 's' : ''}
          </span>
          <span className="text-accent-400/90 tabular-nums">
            {hours > 0 && `${hours}h `}
            {mins > 0 && `${mins}m`} outstanding
          </span>
        </div>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-3">
        {open.map((inst) => (
          <ActivityCard key={inst.id} instance={inst} onToggle={onToggle} showDueDate />
        ))}
      </div>
    </motion.section>
  )
}

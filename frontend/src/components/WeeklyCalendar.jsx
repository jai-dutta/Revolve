import { motion } from 'framer-motion'
import ActivityCard from './ActivityCard'

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']
const DAY_LABELS = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']

function getDateForDay(weekStartDate, dayIndex) {
  const d = new Date(weekStartDate)
  d.setDate(d.getDate() + dayIndex)
  return d
}

function isToday(date) {
  const today = new Date()
  return (
    date.getDate() === today.getDate() &&
    date.getMonth() === today.getMonth() &&
    date.getFullYear() === today.getFullYear()
  )
}

export default function WeeklyCalendar({ instances, weekStartDate, onToggle }) {
  const byDay = Object.fromEntries(DAYS.map((d) => [d, []]))
  instances.forEach((inst) => {
    if (byDay[inst.dayOfWeek]) byDay[inst.dayOfWeek].push(inst)
  })
  DAYS.forEach((d) => byDay[d].sort((a, b) => (a.startTime > b.startTime ? 1 : -1)))

  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-7 gap-3">
      {DAYS.map((day, i) => {
        const date = getDateForDay(weekStartDate, i)
        const today = isToday(date)
        const dayActivities = byDay[day]
        const completedInDay = dayActivities.filter((a) => a.completed).length

        return (
          <motion.div
            key={day}
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.03 }}
            className="flex flex-col min-w-0"
          >
            {/* Day header */}
            <div className="mb-3 flex items-center justify-between">
              <div className="flex items-baseline gap-2">
                <span
                  className={`text-[11px] font-semibold uppercase tracking-[0.08em] ${
                    today ? 'text-accent-400' : 'text-ink-500'
                  }`}
                >
                  {DAY_LABELS[i]}
                </span>
                <span
                  className={`text-sm ${today ? 'text-white font-semibold' : 'text-ink-600'}`}
                >
                  {date.getDate()}
                </span>
              </div>
              {dayActivities.length > 0 && (
                <span className="text-[10px] text-ink-600 tabular-nums">
                  {completedInDay}/{dayActivities.length}
                </span>
              )}
            </div>

            {/* Divider */}
            <div
              className={`h-px mb-3 ${
                today
                  ? 'bg-gradient-to-r from-accent-500/60 via-accent-500/20 to-transparent'
                  : 'bg-ink-800'
              }`}
            />

            {/* Activities */}
            <div className="flex flex-col gap-2 min-h-[80px]">
              {dayActivities.length === 0 ? (
                <div className="flex-1 flex items-center justify-center py-6">
                  <div className="w-6 h-px bg-ink-800" />
                </div>
              ) : (
                dayActivities.map((inst) => (
                  <ActivityCard key={inst.id} instance={inst} onToggle={onToggle} />
                ))
              )}
            </div>
          </motion.div>
        )
      })}
    </div>
  )
}

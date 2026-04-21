import { useState, useEffect } from 'react'
import { AnimatePresence, motion } from 'framer-motion'
import { X, Plus, Pencil, Trash2, GraduationCap, Wrench, Users, FlaskConical, BookOpen, BookMarked } from 'lucide-react'
import { api } from '../api/client'
import { useToast } from './Toast'
import ConfirmDialog from './ConfirmDialog'

const ACTIVITY_TYPES = ['LECTURE', 'WORKSHOP', 'TUTORIAL', 'LABORATORY', 'SEMINAR', 'INDEPENDENT_STUDY']
const TYPE_LABELS = { LECTURE: 'Lecture', WORKSHOP: 'Workshop', TUTORIAL: 'Tutorial', LABORATORY: 'Lab', SEMINAR: 'Seminar', INDEPENDENT_STUDY: 'Self-study' }
const TYPE_ICONS = { LECTURE: GraduationCap, WORKSHOP: Wrench, TUTORIAL: Users, LABORATORY: FlaskConical, SEMINAR: BookOpen, INDEPENDENT_STUDY: BookMarked }

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']
const DAY_LABELS = { MONDAY: 'Monday', TUESDAY: 'Tuesday', WEDNESDAY: 'Wednesday', THURSDAY: 'Thursday', FRIDAY: 'Friday', SATURDAY: 'Saturday', SUNDAY: 'Sunday' }

const EMPTY_FORM = {
  courseName: '',
  activityName: '',
  activityType: 'LECTURE',
  dayOfWeek: 'MONDAY',
  startTime: '09:00',
  durationMinutes: 60,
}

export default function ManageActivitiesModal({ onClose, onSaved }) {
  const toast = useToast()
  const [activities, setActivities] = useState([])
  const [form, setForm] = useState(EMPTY_FORM)
  const [editingId, setEditingId] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState(null)

  useEffect(() => {
    fetchActivities()
  }, [])

  useEffect(() => {
    function onKey(e) { if (e.key === 'Escape' && !deleteTarget) onClose() }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onClose, deleteTarget])

  async function fetchActivities() {
    try {
      const res = await api.activities.list()
      setActivities(res?.data || [])
    } catch (err) {
      toast(err.message, 'error')
    }
  }

  function startEdit(activity) {
    setEditingId(activity.id)
    setForm({
      courseName: activity.courseName,
      activityName: activity.activityName,
      activityType: activity.activityType,
      dayOfWeek: activity.dayOfWeek,
      startTime: activity.startTime.slice(0, 5),
      durationMinutes: activity.durationMinutes,
    })
    setError('')
  }

  function cancelEdit() {
    setEditingId(null)
    setForm(EMPTY_FORM)
    setError('')
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const payload = {
        ...form,
        startTime: form.startTime + (form.startTime.length === 5 ? ':00' : ''),
        durationMinutes: parseInt(form.durationMinutes, 10),
      }
      if (editingId) {
        await api.activities.update(editingId, payload)
        toast('Activity updated', 'success')
      } else {
        await api.activities.create(payload)
        toast('Activity added', 'success')
      }
      await fetchActivities()
      onSaved()
      cancelEdit()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  async function confirmDelete() {
    if (!deleteTarget) return
    const id = deleteTarget.id
    try {
      await api.activities.delete(id)
      toast('Activity removed', 'success')
      await fetchActivities()
      onSaved()
      if (editingId === id) cancelEdit()
    } catch (err) {
      toast(err.message, 'error')
    } finally {
      setDeleteTarget(null)
    }
  }

  // Group by course
  const byCourse = activities.reduce((acc, a) => {
    (acc[a.courseName] ||= []).push(a)
    return acc
  }, {})
  const courses = Object.keys(byCourse).sort()

  return (
    <>
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
        <motion.div
          className="absolute inset-0 bg-ink-950/75 backdrop-blur-md"
          initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
          onClick={onClose}
        />
        <motion.div
          initial={{ opacity: 0, scale: 0.97, y: 8 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          transition={{ type: 'spring', stiffness: 380, damping: 30 }}
          className="relative card-surface w-full max-w-4xl max-h-[90vh] flex flex-col overflow-hidden shadow-soft"
        >
          {/* Header */}
          <div className="flex items-center justify-between px-6 py-4 border-b border-ink-800">
            <div>
              <h2 className="text-base font-semibold text-white">Manage activities</h2>
              <p className="text-xs text-ink-500 mt-0.5">
                Build the weekly rhythm that repeats.
              </p>
            </div>
            <button
              onClick={onClose}
              className="p-1.5 rounded-lg text-ink-500 hover:text-white hover:bg-ink-800 transition-colors"
              aria-label="Close"
            >
              <X className="w-4 h-4" />
            </button>
          </div>

          <div className="flex flex-col md:flex-row flex-1 overflow-hidden">
            {/* List */}
            <div className="md:w-1/2 border-b md:border-b-0 md:border-r border-ink-800 overflow-y-auto">
              {activities.length === 0 ? (
                <div className="p-12 text-center">
                  <p className="text-ink-500 text-sm">No activities yet.</p>
                  <p className="text-ink-600 text-xs mt-1">Add your first on the right.</p>
                </div>
              ) : (
                <div className="p-3 space-y-5">
                  {courses.map((course) => (
                    <div key={course}>
                      <p className="px-2 mb-2 text-[10px] font-semibold uppercase tracking-[0.12em] text-ink-600">
                        {course}
                      </p>
                      <ul className="space-y-1">
                        {byCourse[course].map((a) => {
                          const Icon = TYPE_ICONS[a.activityType]
                          const active = editingId === a.id
                          return (
                            <li key={a.id}>
                              <div
                                className={`group flex items-center gap-3 px-3 py-2.5 rounded-lg border transition-colors cursor-pointer ${
                                  active
                                    ? 'bg-ink-850 border-accent-500/30'
                                    : 'bg-transparent border-transparent hover:bg-ink-900 hover:border-ink-800'
                                }`}
                                onClick={() => startEdit(a)}
                              >
                                <div className="w-7 h-7 rounded-md bg-ink-850 border border-ink-800 flex items-center justify-center shrink-0">
                                  <Icon className="w-3.5 h-3.5 text-ink-300" />
                                </div>
                                <div className="min-w-0 flex-1">
                                  <p className="text-sm font-medium text-white truncate">
                                    {a.activityName}
                                  </p>
                                  <p className="text-[11px] text-ink-500 truncate">
                                    {DAY_LABELS[a.dayOfWeek]} · {a.startTime.slice(0, 5)} · {a.durationMinutes}m
                                  </p>
                                </div>
                                <div className="flex gap-0.5 opacity-0 group-hover:opacity-100 transition-opacity">
                                  <button
                                    onClick={(e) => { e.stopPropagation(); startEdit(a) }}
                                    className="p-1.5 rounded-md text-ink-500 hover:text-white hover:bg-ink-800"
                                    aria-label="Edit"
                                  >
                                    <Pencil className="w-3.5 h-3.5" />
                                  </button>
                                  <button
                                    onClick={(e) => { e.stopPropagation(); setDeleteTarget(a) }}
                                    className="p-1.5 rounded-md text-ink-500 hover:text-rose-400 hover:bg-rose-500/10"
                                    aria-label="Delete"
                                  >
                                    <Trash2 className="w-3.5 h-3.5" />
                                  </button>
                                </div>
                              </div>
                            </li>
                          )
                        })}
                      </ul>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Form */}
            <div className="md:w-1/2 p-6 overflow-y-auto">
              <div className="flex items-center gap-2 mb-5">
                {editingId ? (
                  <>
                    <Pencil className="w-3.5 h-3.5 text-accent-400" />
                    <h3 className="text-xs font-semibold uppercase tracking-[0.1em] text-ink-400">
                      Editing activity
                    </h3>
                  </>
                ) : (
                  <>
                    <Plus className="w-3.5 h-3.5 text-accent-400" />
                    <h3 className="text-xs font-semibold uppercase tracking-[0.1em] text-ink-400">
                      New activity
                    </h3>
                  </>
                )}
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                <Field label="Course">
                  <input
                    value={form.courseName}
                    onChange={(e) => setForm({ ...form, courseName: e.target.value })}
                    required maxLength={64}
                    className="input-field"
                    placeholder="e.g. COMP3001"
                  />
                </Field>

                <Field label="Activity name">
                  <input
                    value={form.activityName}
                    onChange={(e) => setForm({ ...form, activityName: e.target.value })}
                    required maxLength={64}
                    className="input-field"
                    placeholder="e.g. Lecture"
                  />
                </Field>

                <Field label="Type">
                  <div className="grid grid-cols-3 gap-1.5">
                    {ACTIVITY_TYPES.map((t) => {
                      const Icon = TYPE_ICONS[t]
                      const active = form.activityType === t
                      return (
                        <button
                          type="button" key={t}
                          onClick={() => setForm({ ...form, activityType: t })}
                          className={`flex items-center gap-1.5 px-2.5 py-2 rounded-lg text-xs transition-colors border ${
                            active
                              ? 'bg-ink-800 border-accent-500/30 text-white'
                              : 'bg-transparent border-ink-800 text-ink-400 hover:text-white hover:border-ink-700'
                          }`}
                        >
                          <Icon className="w-3 h-3 shrink-0" />
                          <span className="truncate">{TYPE_LABELS[t]}</span>
                        </button>
                      )
                    })}
                  </div>
                </Field>

                <Field label="Day">
                  <div className="grid grid-cols-7 gap-1">
                    {DAYS.map((d) => {
                      const active = form.dayOfWeek === d
                      return (
                        <button
                          type="button" key={d}
                          onClick={() => setForm({ ...form, dayOfWeek: d })}
                          className={`py-2 text-xs font-medium rounded-md transition-colors border ${
                            active
                              ? 'bg-ink-800 border-accent-500/30 text-white'
                              : 'bg-transparent border-ink-800 text-ink-400 hover:text-white hover:border-ink-700'
                          }`}
                        >
                          {d[0]}
                        </button>
                      )
                    })}
                  </div>
                  <p className="text-[11px] text-ink-600 mt-1.5">{DAY_LABELS[form.dayOfWeek]}</p>
                </Field>

                <div className="grid grid-cols-2 gap-3">
                  <Field label="Start">
                    <input
                      type="time"
                      value={form.startTime}
                      onChange={(e) => setForm({ ...form, startTime: e.target.value })}
                      required className="input-field"
                    />
                  </Field>
                  <Field label="Duration (min)">
                    <input
                      type="number"
                      value={form.durationMinutes}
                      onChange={(e) => setForm({ ...form, durationMinutes: e.target.value })}
                      required min={5} max={480} step={5}
                      className="input-field"
                    />
                  </Field>
                </div>

                {error && <p className="text-rose-400 text-xs">{error}</p>}

                <div className="flex gap-2 pt-1">
                  <button type="submit" disabled={loading} className="btn-primary flex-1 !py-2.5">
                    {loading ? 'Saving…' : editingId ? 'Update activity' : 'Add activity'}
                  </button>
                  {editingId && (
                    <button type="button" onClick={cancelEdit} className="btn-outline px-4">
                      Cancel
                    </button>
                  )}
                </div>
              </form>
            </div>
          </div>
        </motion.div>
      </div>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete this activity?"
        description={deleteTarget && `"${deleteTarget.activityName}" and all its instances will be removed from your calendar and backlog.`}
        confirmLabel="Delete"
        destructive
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </>
  )
}

function Field({ label, children }) {
  return (
    <div>
      <label className="label">{label}</label>
      {children}
    </div>
  )
}

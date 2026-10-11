import { useEffect, useState } from 'react'
import { api, type ScheduleEvent } from '../../../lib/api'

export default function AdminSchedule() {
  const [items, setItems] = useState<ScheduleEvent[]>([])
  const [showForm, setShowForm] = useState(false)
  const [title, setTitle] = useState('')
  const [eventAt, setEventAt] = useState('')
  const [location, setLocation] = useState('')
  const [type, setType] = useState('PROGRAM')
  const [loading, setLoading] = useState(false)

  useEffect(() => { api.admin.schedule.list().then(setItems) }, [])

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    try {
      const created = await api.admin.schedule.create({ title, eventAt: new Date(eventAt).toISOString(), location: location || undefined, type })
      setItems([created, ...items])
      setTitle(''); setEventAt(''); setLocation(''); setShowForm(false)
    } finally {
      setLoading(false)
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('মুছে দিতে চান?')) return
    await api.admin.schedule.delete(id)
    setItems(items.filter((s) => s.id !== id))
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-bold text-gray-900 font-bn">সময়সূচি</h1>
        <button onClick={() => setShowForm(!showForm)}
          className="bg-brand-700 text-white px-4 py-2 rounded-lg text-sm font-bn hover:bg-brand-800">
          + নতুন
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleCreate} className="bg-white rounded-xl p-4 shadow-sm mb-4 space-y-3">
          <input type="text" placeholder="শিরোনাম *" value={title} onChange={(e) => setTitle(e.target.value)} required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn focus:ring-2 focus:ring-brand-600" />
          <input type="datetime-local" value={eventAt} onChange={(e) => setEventAt(e.target.value)} required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-brand-600" />
          <input type="text" placeholder="স্থান" value={location} onChange={(e) => setLocation(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn focus:ring-2 focus:ring-brand-600" />
          <select value={type} onChange={(e) => setType(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn">
            {['DARS', 'PROGRAM', 'MEETING', 'ONLINE'].map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
          <div className="flex gap-2">
            <button type="submit" disabled={loading}
              className="bg-brand-700 text-white px-4 py-2 rounded-lg text-sm font-bn disabled:opacity-50">সংরক্ষণ</button>
            <button type="button" onClick={() => setShowForm(false)}
              className="bg-gray-100 text-gray-700 px-4 py-2 rounded-lg text-sm font-bn">বাতিল</button>
          </div>
        </form>
      )}

      <div className="space-y-3">
        {items.map((s) => (
          <div key={s.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex items-start justify-between">
            <div>
              <p className="font-semibold text-gray-900 font-bn">{s.title}</p>
              <p className="text-sm text-gray-500 mt-1">
                {new Date(s.eventAt).toLocaleString('bn-BD')}
                {s.location && ` · ${s.location}`}
              </p>
            </div>
            <button onClick={() => handleDelete(s.id)} className="text-red-400 hover:text-red-600 ml-4">✕</button>
          </div>
        ))}
      </div>
    </div>
  )
}

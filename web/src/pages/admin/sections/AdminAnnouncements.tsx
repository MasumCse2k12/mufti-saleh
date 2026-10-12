import { useEffect, useState } from 'react'
import { api, type Announcement } from '../../../lib/api'

export default function AdminAnnouncements() {
  const [items, setItems] = useState<Announcement[]>([])
  const [showForm, setShowForm] = useState(false)
  const [title, setTitle] = useState('')
  const [body, setBody] = useState('')
  const [pinned, setPinned] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    api.admin.announcements.list().then(setItems).catch(() => setError('ঘোষণা লোড করতে সমস্যা হয়েছে।'))
  }, [])

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      const created = await api.admin.announcements.create({
        title, body, pinned,
        publishedAt: new Date().toISOString(),
      })
      setItems([created, ...items])
      setTitle(''); setBody(''); setPinned(false); setShowForm(false)
    } catch (err: unknown) {
      const msg = (err as { messageBn?: string; detail?: string })
      setError(msg.messageBn ?? msg.detail ?? 'ঘোষণা সংরক্ষণ করতে সমস্যা হয়েছে।')
    } finally {
      setLoading(false)
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('মুছে দিতে চান?')) return
    await api.admin.announcements.delete(id)
    setItems(items.filter((a) => a.id !== id))
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-bold text-gray-900 font-bn">ঘোষণা</h1>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-brand-700 text-white px-4 py-2 rounded-lg text-sm font-bn hover:bg-brand-800"
        >
          + নতুন
        </button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 text-sm font-bn mb-4">
          {error}
        </div>
      )}

      {showForm && (
        <form onSubmit={handleCreate} className="bg-white rounded-xl p-4 shadow-sm mb-4 space-y-3">
          <input
            type="text" placeholder="শিরোনাম" value={title} onChange={(e) => setTitle(e.target.value)}
            required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn focus:ring-2 focus:ring-brand-600"
          />
          <textarea
            placeholder="বিবরণ" value={body} onChange={(e) => setBody(e.target.value)} rows={4}
            required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn focus:ring-2 focus:ring-brand-600 resize-none"
          />
          <label className="flex items-center gap-2 font-bn text-sm">
            <input type="checkbox" checked={pinned} onChange={(e) => setPinned(e.target.checked)} />
            পিন করুন
          </label>
          <div className="flex gap-2">
            <button type="submit" disabled={loading}
              className="bg-brand-700 text-white px-4 py-2 rounded-lg text-sm font-bn disabled:opacity-50">
              সংরক্ষণ করুন
            </button>
            <button type="button" onClick={() => setShowForm(false)}
              className="bg-gray-100 text-gray-700 px-4 py-2 rounded-lg text-sm font-bn">
              বাতিল
            </button>
          </div>
        </form>
      )}

      <div className="space-y-3">
        {items.map((a) => (
          <div key={a.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
            <div className="flex items-start justify-between">
              <div>
                {a.pinned && <span className="text-xs bg-amber-100 text-amber-700 px-2 py-0.5 rounded mr-2 font-bn">পিন</span>}
                <span className="font-semibold text-gray-900 font-bn">{a.title}</span>
              </div>
              <button onClick={() => handleDelete(a.id)} className="text-red-400 hover:text-red-600 text-sm ml-4">✕</button>
            </div>
            <p className="text-gray-600 text-sm font-bn mt-2 line-clamp-3">{a.body}</p>
          </div>
        ))}
      </div>
    </div>
  )
}

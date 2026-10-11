import { useEffect, useState } from 'react'
import { api, type MediaItem } from '../../../lib/api'

export default function AdminMedia() {
  const [items, setItems] = useState<MediaItem[]>([])
  const [showForm, setShowForm] = useState(false)
  const [title, setTitle] = useState('')
  const [youtubeId, setYoutubeId] = useState('')
  const [type, setType] = useState('BAYAAN')
  const [description, setDescription] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => { api.media.list().then(setItems) }, [])

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    try {
      const created = await api.admin.media.create({
        title, youtubeId: youtubeId || undefined,
        type, description: description || undefined,
        publishedAt: new Date().toISOString()
      })
      setItems([created as MediaItem, ...items])
      setTitle(''); setYoutubeId(''); setDescription(''); setShowForm(false)
    } finally {
      setLoading(false)
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('মুছে দিতে চান?')) return
    await api.admin.media.delete(id)
    setItems(items.filter((m) => m.id !== id))
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-bold text-gray-900 font-bn">মিডিয়া</h1>
        <button onClick={() => setShowForm(!showForm)}
          className="bg-brand-700 text-white px-4 py-2 rounded-lg text-sm font-bn hover:bg-brand-800">
          + নতুন
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleCreate} className="bg-white rounded-xl p-4 shadow-sm mb-4 space-y-3">
          <input type="text" placeholder="শিরোনাম *" value={title} onChange={(e) => setTitle(e.target.value)} required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn focus:ring-2 focus:ring-brand-600" />
          <input type="text" placeholder="YouTube Video ID (e.g. dQw4w9WgXcQ)" value={youtubeId} onChange={(e) => setYoutubeId(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-brand-600" />
          <select value={type} onChange={(e) => setType(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn focus:ring-2 focus:ring-brand-600">
            {['BAYAAN', 'TAFSIR', 'ISLAHI', 'DARS', 'OTHER'].map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
          <textarea placeholder="বিবরণ" value={description} onChange={(e) => setDescription(e.target.value)} rows={3}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn resize-none focus:ring-2 focus:ring-brand-600" />
          <div className="flex gap-2">
            <button type="submit" disabled={loading}
              className="bg-brand-700 text-white px-4 py-2 rounded-lg text-sm font-bn disabled:opacity-50">
              সংরক্ষণ
            </button>
            <button type="button" onClick={() => setShowForm(false)}
              className="bg-gray-100 text-gray-700 px-4 py-2 rounded-lg text-sm font-bn">
              বাতিল
            </button>
          </div>
        </form>
      )}

      <div className="space-y-3">
        {items.map((m) => (
          <div key={m.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex gap-4">
            {m.youtubeId && (
              <img src={`https://img.youtube.com/vi/${m.youtubeId}/mqdefault.jpg`}
                className="w-24 h-16 object-cover rounded-lg flex-shrink-0" alt="" />
            )}
            <div className="flex-1">
              <p className="font-semibold text-gray-900 font-bn">{m.title}</p>
              <p className="text-xs text-gray-400 mt-1">{m.type}</p>
            </div>
            <button onClick={() => handleDelete(m.id)} className="text-red-400 hover:text-red-600">✕</button>
          </div>
        ))}
      </div>
    </div>
  )
}

import { useEffect, useState } from 'react'
import { api, type Masail } from '../../../lib/api'

export default function AdminMasail() {
  const [items, setItems] = useState<Masail[]>([])
  const [showForm, setShowForm] = useState(false)
  const [question, setQuestion] = useState('')
  const [answer, setAnswer] = useState('')
  const [category, setCategory] = useState('MISC')
  const [loading, setLoading] = useState(false)

  useEffect(() => { api.admin.masail.list().then(setItems) }, [])

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    try {
      const created = await api.admin.masail.create({ question, answer, category, published: true })
      setItems([created, ...items])
      setQuestion(''); setAnswer(''); setShowForm(false)
    } finally {
      setLoading(false)
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('মুছে দিতে চান?')) return
    await api.admin.masail.delete(id)
    setItems(items.filter((m) => m.id !== id))
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-bold text-gray-900 font-bn">মহিলাদের মাসায়েল</h1>
        <button onClick={() => setShowForm(!showForm)}
          className="bg-brand-700 text-white px-4 py-2 rounded-lg text-sm font-bn hover:bg-brand-800">
          + নতুন
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleCreate} className="bg-white rounded-xl p-4 shadow-sm mb-4 space-y-3">
          <select value={category} onChange={(e) => setCategory(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn">
            {['TAHARA', 'SALAT', 'SAWM', 'NIKAH', 'MISC'].map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
          <textarea placeholder="প্রশ্ন *" value={question} onChange={(e) => setQuestion(e.target.value)} rows={3} required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn resize-none focus:ring-2 focus:ring-brand-600" />
          <textarea placeholder="উত্তর *" value={answer} onChange={(e) => setAnswer(e.target.value)} rows={4} required
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
          <div key={m.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
            <div className="flex items-start justify-between">
              <p className="font-medium text-gray-900 font-bn flex-1">{m.question}</p>
              <button onClick={() => handleDelete(m.id)} className="text-red-400 hover:text-red-600 ml-4">✕</button>
            </div>
            <p className="text-gray-600 text-sm font-bn mt-2 line-clamp-3">{m.answer}</p>
            <span className="text-xs text-gray-400 font-bn mt-1 inline-block">{m.category}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

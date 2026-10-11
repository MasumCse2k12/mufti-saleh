import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, type Announcement } from '../lib/api'

export default function Announcements() {
  const [items, setItems] = useState<Announcement[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.announcements.list().then(setItems).finally(() => setLoading(false))
  }, [])

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-brand-700 text-white px-4 py-4 flex items-center gap-3">
        <Link to="/" className="text-white/80 hover:text-white">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </Link>
        <h1 className="text-lg font-semibold font-bn">জরুরি ঘোষণা</h1>
      </header>

      <main className="max-w-2xl mx-auto px-4 py-4 space-y-3">
        {loading && <p className="text-center text-gray-500 py-8 font-bn">লোড হচ্ছে...</p>}
        {!loading && items.length === 0 && (
          <p className="text-center text-gray-500 py-8 font-bn">কোনো ঘোষণা নেই।</p>
        )}
        {items.map((a) => (
          <div key={a.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
            {a.pinned && (
              <span className="inline-block bg-amber-100 text-amber-700 text-xs px-2 py-0.5 rounded mb-2 font-bn">
                পিন করা
              </span>
            )}
            <h2 className="font-semibold text-gray-900 font-bn">{a.title}</h2>
            <p className="text-gray-600 text-sm mt-2 font-bn leading-relaxed whitespace-pre-wrap">{a.body}</p>
            <p className="text-gray-400 text-xs mt-3 font-bn">
              {new Date(a.publishedAt).toLocaleDateString('bn-BD', { dateStyle: 'medium' })}
            </p>
          </div>
        ))}
      </main>
    </div>
  )
}

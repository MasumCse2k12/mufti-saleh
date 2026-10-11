import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, type MediaItem } from '../lib/api'

const TYPES = [
  { value: '', label: 'সব' },
  { value: 'BAYAAN', label: 'বয়ান' },
  { value: 'TAFSIR', label: 'তাফসির' },
  { value: 'ISLAHI', label: 'ইসলাহি' },
  { value: 'DARS', label: 'দরস' },
]

export default function Media() {
  const [items, setItems] = useState<MediaItem[]>([])
  const [type, setType] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    api.media.list(type || undefined).then(setItems).finally(() => setLoading(false))
  }, [type])

  function formatDuration(seconds: number): string {
    const m = Math.floor(seconds / 60)
    const s = seconds % 60
    return `${m}:${String(s).padStart(2, '0')}`
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-brand-700 text-white px-4 py-4 flex items-center gap-3">
        <Link to="/" className="text-white/80 hover:text-white">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </Link>
        <h1 className="text-lg font-semibold font-bn">শায়খের আলোচনা</h1>
      </header>

      <div className="max-w-2xl mx-auto px-4 pt-4 overflow-x-auto">
        <div className="flex gap-2 pb-2">
          {TYPES.map((t) => (
            <button
              key={t.value}
              onClick={() => setType(t.value)}
              className={`flex-shrink-0 px-3 py-1.5 rounded-full text-sm font-bn transition-colors ${
                type === t.value
                  ? 'bg-brand-700 text-white'
                  : 'bg-white text-gray-600 border border-gray-200'
              }`}
            >
              {t.label}
            </button>
          ))}
        </div>
      </div>

      <main className="max-w-2xl mx-auto px-4 py-3 space-y-3">
        {loading && <p className="text-center text-gray-500 py-8 font-bn">লোড হচ্ছে...</p>}
        {!loading && items.length === 0 && (
          <p className="text-center text-gray-500 py-8 font-bn">কোনো ভিডিও নেই।</p>
        )}
        {items.map((m) => (
          <div key={m.id} className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            {m.youtubeId && (
              <a
                href={`https://www.youtube.com/watch?v=${m.youtubeId}`}
                target="_blank"
                rel="noopener noreferrer"
                className="block relative"
              >
                <img
                  src={`https://img.youtube.com/vi/${m.youtubeId}/mqdefault.jpg`}
                  alt={m.title}
                  className="w-full h-48 object-cover"
                />
                <div className="absolute inset-0 flex items-center justify-center bg-black/20 hover:bg-black/30 transition-colors">
                  <div className="w-12 h-12 bg-white/90 rounded-full flex items-center justify-center">
                    <svg className="w-6 h-6 text-brand-700 ml-1" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M8 5v14l11-7z" />
                    </svg>
                  </div>
                </div>
              </a>
            )}
            <div className="p-3">
              <h2 className="font-semibold text-gray-900 font-bn">{m.title}</h2>
              {m.description && (
                <p className="text-sm text-gray-500 font-bn mt-1 line-clamp-2">{m.description}</p>
              )}
              <div className="flex items-center gap-3 mt-2 text-xs text-gray-400">
                <span className="bg-brand-50 text-brand-700 px-2 py-0.5 rounded font-bn">
                  {TYPES.find((t) => t.value === m.type)?.label ?? m.type}
                </span>
                {m.durationSeconds && <span>{formatDuration(m.durationSeconds)}</span>}
                <span>{new Date(m.publishedAt).toLocaleDateString('bn-BD', { dateStyle: 'medium' })}</span>
              </div>
            </div>
          </div>
        ))}
      </main>
    </div>
  )
}

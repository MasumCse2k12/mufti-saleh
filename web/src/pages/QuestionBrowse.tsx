import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, type Question } from '../lib/api'

const CATEGORIES = [
  { value: '', label: 'সব বিষয়' },
  { value: 'TAHARAH', label: 'পবিত্রতা' },
  { value: 'SALAT', label: 'নামাজ' },
  { value: 'SAWM', label: 'রোজা' },
  { value: 'ZAKAT', label: 'যাকাত' },
  { value: 'NIKAH', label: 'বিবাহ' },
  { value: 'TALAQ', label: 'তালাক' },
  { value: 'WOMENS_ISSUES', label: 'মহিলাদের মাসায়েল' },
  { value: 'MUAMALAT', label: 'লেনদেন' },
  { value: 'AQIDAH', label: 'আকিদা' },
  { value: 'GENERAL', label: 'সাধারণ' },
]

export default function QuestionBrowse() {
  const [items, setItems] = useState<Question[]>([])
  const [category, setCategory] = useState('')
  const [expanded, setExpanded] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    api.questions.list(category || undefined).then(setItems).finally(() => setLoading(false))
  }, [category])

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-brand-700 text-white px-4 py-4 flex items-center gap-3">
        <Link to="/" className="text-white/80 hover:text-white">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </Link>
        <h1 className="text-lg font-semibold font-bn">পূর্বের প্রশ্ন</h1>
      </header>

      <div className="max-w-2xl mx-auto px-4 pt-4 overflow-x-auto">
        <div className="flex gap-2 pb-2">
          {CATEGORIES.map((c) => (
            <button
              key={c.value}
              onClick={() => setCategory(c.value)}
              className={`flex-shrink-0 px-3 py-1.5 rounded-full text-sm font-bn transition-colors ${
                category === c.value
                  ? 'bg-brand-700 text-white'
                  : 'bg-white text-gray-600 border border-gray-200 hover:border-brand-500'
              }`}
            >
              {c.label}
            </button>
          ))}
        </div>
      </div>

      <main className="max-w-2xl mx-auto px-4 py-3 space-y-3">
        {loading && <p className="text-center text-gray-500 py-8 font-bn">লোড হচ্ছে...</p>}
        {!loading && items.length === 0 && (
          <p className="text-center text-gray-500 py-8 font-bn">এই বিষয়ে কোনো প্রশ্ন নেই।</p>
        )}
        {items.map((q) => (
          <div key={q.id} className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            <button
              onClick={() => setExpanded(expanded === q.id ? null : q.id)}
              className="w-full text-left p-4"
            >
              <p className="font-medium text-gray-900 font-bn leading-relaxed">{q.body}</p>
              <span className="inline-block mt-2 text-xs text-brand-700 bg-brand-50 px-2 py-0.5 rounded font-bn">
                {CATEGORIES.find((c) => c.value === q.category)?.label ?? q.category}
              </span>
            </button>
            {expanded === q.id && q.answer && (
              <div className="px-4 pb-4 pt-0 border-t border-gray-100">
                <p className="text-sm font-semibold text-brand-800 font-bn mb-1">উত্তর:</p>
                <p className="text-gray-700 text-sm font-bn leading-relaxed whitespace-pre-wrap">{q.answer}</p>
              </div>
            )}
          </div>
        ))}
      </main>
    </div>
  )
}

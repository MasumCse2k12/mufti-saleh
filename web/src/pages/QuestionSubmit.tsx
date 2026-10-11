import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../lib/api'

const CATEGORIES = [
  { value: 'GENERAL', label: 'সাধারণ' },
  { value: 'TAHARAH', label: 'পবিত্রতা' },
  { value: 'SALAT', label: 'নামাজ' },
  { value: 'SAWM', label: 'রোজা' },
  { value: 'ZAKAT', label: 'যাকাত' },
  { value: 'NIKAH', label: 'বিবাহ' },
  { value: 'TALAQ', label: 'তালাক' },
  { value: 'WOMENS_ISSUES', label: 'মহিলাদের মাসায়েল' },
  { value: 'MUAMALAT', label: 'লেনদেন' },
  { value: 'AQIDAH', label: 'আকিদা' },
]

export default function QuestionSubmit() {
  const [body, setBody] = useState('')
  const [category, setCategory] = useState('GENERAL')
  const [name, setName] = useState('')
  const [contact, setContact] = useState('')
  const [submitted, setSubmitted] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!body.trim()) return
    setLoading(true)
    setError('')
    try {
      await api.questions.submit(body, category, name || undefined, contact || undefined)
      setSubmitted(true)
    } catch {
      setError('প্রশ্ন পাঠানো সম্ভব হয়নি। আবার চেষ্টা করুন।')
    } finally {
      setLoading(false)
    }
  }

  if (submitted) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center px-4">
        <div className="bg-white rounded-xl p-8 shadow text-center max-w-sm w-full">
          <div className="text-4xl mb-4">✅</div>
          <h2 className="text-xl font-semibold text-gray-900 font-bn mb-2">জাযাকাল্লাহ খায়রান</h2>
          <p className="text-gray-600 font-bn text-sm">আপনার প্রশ্ন সফলভাবে পাঠানো হয়েছে।</p>
          <Link to="/" className="mt-6 inline-block bg-brand-700 text-white px-6 py-2 rounded-lg font-bn">
            হোমে ফিরুন
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-brand-700 text-white px-4 py-4 flex items-center gap-3">
        <Link to="/" className="text-white/80 hover:text-white">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </Link>
        <h1 className="text-lg font-semibold font-bn">প্রশ্ন জমা দিন</h1>
      </header>

      <main className="max-w-2xl mx-auto px-4 py-4">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 font-bn mb-1">বিষয় *</label>
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2.5 font-bn focus:ring-2 focus:ring-brand-600 focus:border-transparent"
            >
              {CATEGORIES.map((c) => (
                <option key={c.value} value={c.value}>{c.label}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 font-bn mb-1">প্রশ্ন লিখুন *</label>
            <textarea
              value={body}
              onChange={(e) => setBody(e.target.value)}
              rows={6}
              maxLength={2000}
              placeholder="আপনার দীনি প্রশ্ন এখানে লিখুন..."
              className="w-full border border-gray-300 rounded-lg px-3 py-2.5 font-bn focus:ring-2 focus:ring-brand-600 focus:border-transparent resize-none"
              required
            />
            <p className="text-xs text-gray-400 text-right mt-1">{body.length}/2000</p>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 font-bn mb-1">আপনার নাম (ঐচ্ছিক)</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="মো. আবদুল্লাহ"
              className="w-full border border-gray-300 rounded-lg px-3 py-2.5 font-bn focus:ring-2 focus:ring-brand-600 focus:border-transparent"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 font-bn mb-1">যোগাযোগ (ঐচ্ছিক)</label>
            <input
              type="text"
              value={contact}
              onChange={(e) => setContact(e.target.value)}
              placeholder="ফোন নম্বর বা ইমেইল"
              className="w-full border border-gray-300 rounded-lg px-3 py-2.5 font-bn focus:ring-2 focus:ring-brand-600 focus:border-transparent"
            />
          </div>

          {error && <p className="text-red-600 text-sm font-bn">{error}</p>}

          <button
            type="submit"
            disabled={loading || !body.trim()}
            className="w-full bg-brand-700 text-white py-3 rounded-xl font-semibold font-bn
                       hover:bg-brand-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {loading ? 'পাঠানো হচ্ছে...' : 'প্রশ্ন পাঠান'}
          </button>
        </form>
      </main>
    </div>
  )
}

import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../lib/api'

export default function Bayah() {
  const [showForm, setShowForm] = useState(false)
  const [name, setName] = useState('')
  const [phone, setPhone] = useState('')
  const [district, setDistrict] = useState('')
  const [note, setNote] = useState('')
  const [submitted, setSubmitted] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      await api.bayah.submit({ name, phone: phone || undefined, district: district || undefined, note: note || undefined })
      setSubmitted(true)
    } catch {
      setError('আবেদন পাঠানো সম্ভব হয়নি। আবার চেষ্টা করুন।')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-brand-700 text-white px-4 py-4 flex items-center gap-3">
        <Link to="/" className="text-white/80 hover:text-white">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </Link>
        <h1 className="text-lg font-semibold font-bn">বাইয়াত</h1>
      </header>

      <main className="max-w-2xl mx-auto px-4 py-4 space-y-4">
        <div className="bg-white rounded-xl p-4 shadow-sm">
          <h2 className="font-semibold text-gray-900 font-bn mb-2">বাইয়াত কী?</h2>
          <p className="text-gray-600 font-bn text-sm leading-relaxed">
            বাইয়াত হলো একজন আধ্যাত্মিক পথপ্রদর্শকের হাতে আনুগত্যের শপথ নেওয়া, যার মাধ্যমে
            মুরিদ তার আত্মার পরিশুদ্ধির পথে পরিচালিত হয়। এটি কুরআন ও সুন্নাহর ভিত্তিতে
            প্রতিষ্ঠিত একটি পবিত্র সম্পর্ক।
          </p>
        </div>

        <div className="bg-white rounded-xl p-4 shadow-sm">
          <h2 className="font-semibold text-gray-900 font-bn mb-2">বাইয়াতের শর্তসমূহ</h2>
          <ul className="space-y-2 text-sm text-gray-600 font-bn">
            {[
              'সত্যিকারের তওবার সাথে বাইয়াত নেওয়ার নিয়্যাত',
              'শায়খের নির্দেশনা মেনে চলার প্রতিশ্রুতি',
              'নিয়মিত যিকির ও ইবাদতে মনোযোগ',
            ].map((item, i) => (
              <li key={i} className="flex items-start gap-2">
                <span className="text-brand-700 font-bold mt-0.5">{i + 1}.</span>
                <span>{item}</span>
              </li>
            ))}
          </ul>
        </div>

        {!submitted ? (
          !showForm ? (
            <button
              onClick={() => setShowForm(true)}
              className="w-full bg-brand-700 text-white py-3 rounded-xl font-semibold font-bn hover:bg-brand-800 transition-colors"
            >
              বাইয়াতের আবেদন করুন
            </button>
          ) : (
            <form onSubmit={handleSubmit} className="bg-white rounded-xl p-4 shadow-sm space-y-4">
              <h2 className="font-semibold text-gray-900 font-bn">আবেদন ফর্ম</h2>

              <div>
                <label className="block text-sm font-medium text-gray-700 font-bn mb-1">নাম *</label>
                <input
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                  className="w-full border border-gray-300 rounded-lg px-3 py-2.5 font-bn focus:ring-2 focus:ring-brand-600"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 font-bn mb-1">ফোন নম্বর</label>
                <input
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2.5 font-bn focus:ring-2 focus:ring-brand-600"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 font-bn mb-1">জেলা</label>
                <input
                  type="text"
                  value={district}
                  onChange={(e) => setDistrict(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2.5 font-bn focus:ring-2 focus:ring-brand-600"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 font-bn mb-1">সংক্ষিপ্ত পরিচয় / বার্তা</label>
                <textarea
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  rows={4}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2.5 font-bn focus:ring-2 focus:ring-brand-600 resize-none"
                />
              </div>
              {error && <p className="text-red-600 text-sm font-bn">{error}</p>}
              <button
                type="submit"
                disabled={loading || !name.trim()}
                className="w-full bg-brand-700 text-white py-3 rounded-xl font-semibold font-bn hover:bg-brand-800 disabled:opacity-50 transition-colors"
              >
                {loading ? 'পাঠানো হচ্ছে...' : 'আবেদন পাঠান'}
              </button>
            </form>
          )
        ) : (
          <div className="bg-white rounded-xl p-8 shadow text-center">
            <div className="text-4xl mb-4">✅</div>
            <p className="text-gray-700 font-bn">আপনার আবেদন পাঠানো হয়েছে। শীঘ্রই যোগাযোগ করা হবে।</p>
            <Link to="/" className="mt-4 inline-block text-brand-700 font-bn font-medium">হোমে ফিরুন</Link>
          </div>
        )}
      </main>
    </div>
  )
}

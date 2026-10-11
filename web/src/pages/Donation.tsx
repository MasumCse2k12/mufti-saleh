import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, type DonationSummary } from '../lib/api'

const PURPOSE_LABELS: Record<string, string> = {
  SADAQAH_JARIYAH: 'সদকায়ে জারিয়া',
  LILLAH: 'লিল্লাহ ফান্ড',
  BOOKS: 'কিতাব ক্রয়',
  MADRASA_FUND: 'মাদরাসা ফান্ড',
  OTHER: 'অন্যান্য',
}

export default function Donation() {
  const [summary, setSummary] = useState<DonationSummary | null>(null)

  useEffect(() => {
    api.donations.summary().then(setSummary).catch(() => {})
  }, [])

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-brand-700 text-white px-4 py-4 flex items-center gap-3">
        <Link to="/" className="text-white/80 hover:text-white">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </Link>
        <h1 className="text-lg font-semibold font-bn">মাদরাসায় ডোনেশন</h1>
      </header>

      <main className="max-w-2xl mx-auto px-4 py-4 space-y-4">
        <div className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
          <h2 className="font-semibold text-gray-900 font-bn mb-3">ডোনেশনের খাতসমূহ</h2>
          <div className="space-y-3">
            {[
              { key: 'SADAQAH_JARIYAH', desc: 'মৃত ব্যক্তির আত্মার ঈসালে সওয়াবের জন্য' },
              { key: 'LILLAH', desc: 'আল্লাহর রাস্তায় সাধারণ দান' },
              { key: 'BOOKS', desc: 'দীনি কিতাব সংগ্রহ ও বিতরণ' },
              { key: 'MADRASA_FUND', desc: 'মাদরাসার সাধারণ ফান্ড' },
            ].map((p) => (
              <div key={p.key} className="flex items-start gap-3">
                <div className="w-2 h-2 rounded-full bg-brand-700 mt-2 flex-shrink-0" />
                <div>
                  <p className="font-medium font-bn text-gray-900">{PURPOSE_LABELS[p.key]}</p>
                  <p className="text-sm text-gray-500 font-bn">{p.desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {summary && (
          <div className="bg-brand-50 rounded-xl p-4 border border-brand-100">
            <h2 className="font-semibold text-brand-900 font-bn mb-3">মোট সংগ্রহ</h2>
            <div className="space-y-2">
              {Object.entries(summary.byPurpose).map(([purpose, amount]) => (
                <div key={purpose} className="flex justify-between text-sm font-bn">
                  <span className="text-gray-700">{PURPOSE_LABELS[purpose] ?? purpose}</span>
                  <span className="font-semibold text-brand-800">৳ {Number(amount).toLocaleString('bn-BD')}</span>
                </div>
              ))}
              <div className="flex justify-between font-semibold font-bn border-t border-brand-200 pt-2 mt-2">
                <span className="text-brand-900">সর্বমোট</span>
                <span className="text-brand-700">৳ {Number(summary.total).toLocaleString('bn-BD')}</span>
              </div>
            </div>
          </div>
        )}

        <div className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
          <h2 className="font-semibold text-gray-900 font-bn mb-2">কীভাবে ডোনেশন করবেন?</h2>
          <p className="text-gray-600 font-bn text-sm leading-relaxed">
            বিকাশ বা নগদে সরাসরি মাদরাসার নম্বরে পাঠান অথবা সরাসরি যোগাযোগ করুন।
            ডোনেশন রেকর্ড করার জন্য মাদরাসা কর্তৃপক্ষের সাথে যোগাযোগ করুন।
          </p>
        </div>
      </main>
    </div>
  )
}

import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, type ScheduleEvent } from '../lib/api'

const TYPE_LABELS: Record<string, string> = {
  DARS: 'দরস',
  PROGRAM: 'মাহফিল',
  MEETING: 'সাক্ষাৎ',
  ONLINE: 'অনলাইন',
}

export default function Schedule() {
  const [events, setEvents] = useState<ScheduleEvent[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.schedule.upcoming().then(setEvents).finally(() => setLoading(false))
  }, [])

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-brand-700 text-white px-4 py-4 flex items-center gap-3">
        <Link to="/" className="text-white/80 hover:text-white">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </Link>
        <h1 className="text-lg font-semibold font-bn">সময়সূচি</h1>
      </header>

      <main className="max-w-2xl mx-auto px-4 py-4 space-y-3">
        {loading && <p className="text-center text-gray-500 py-8 font-bn">লোড হচ্ছে...</p>}
        {!loading && events.length === 0 && (
          <p className="text-center text-gray-500 py-8 font-bn">আপাতত কোনো নির্ধারিত অনুষ্ঠান নেই।</p>
        )}
        {events.map((e) => {
          const date = new Date(e.eventAt)
          return (
            <div key={e.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex gap-4">
              <div className="flex-shrink-0 text-center bg-brand-50 rounded-lg px-3 py-2 min-w-[56px]">
                <p className="text-2xl font-bold text-brand-700 leading-none">
                  {date.getDate()}
                </p>
                <p className="text-xs text-brand-600 font-bn mt-0.5">
                  {date.toLocaleDateString('bn-BD', { month: 'short' })}
                </p>
              </div>
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-1">
                  <h2 className="font-semibold text-gray-900 font-bn">{e.title}</h2>
                  <span className="text-xs bg-brand-100 text-brand-700 px-2 py-0.5 rounded font-bn">
                    {TYPE_LABELS[e.type] ?? e.type}
                  </span>
                </div>
                {e.description && (
                  <p className="text-sm text-gray-600 font-bn">{e.description}</p>
                )}
                {e.location && (
                  <p className="text-xs text-gray-400 font-bn mt-1">📍 {e.location}</p>
                )}
                <p className="text-xs text-gray-400 mt-1">
                  {date.toLocaleTimeString('bn-BD', { hour: '2-digit', minute: '2-digit' })}
                </p>
              </div>
            </div>
          )
        })}
      </main>
    </div>
  )
}

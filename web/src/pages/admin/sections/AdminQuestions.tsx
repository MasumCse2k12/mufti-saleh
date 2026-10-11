import { useEffect, useState } from 'react'
import { api, type QuestionAdmin } from '../../../lib/api'

export default function AdminQuestions() {
  const [items, setItems] = useState<QuestionAdmin[]>([])
  const [pending, setPending] = useState(true)
  const [answer, setAnswer] = useState<Record<string, string>>({})

  useEffect(() => {
    api.admin.questions.list(pending).then(setItems)
  }, [pending])

  async function handleAnswer(id: string) {
    const text = answer[id]
    if (!text?.trim()) return
    const updated = await api.admin.questions.answer(id, text)
    setItems(items.map((q) => q.id === id ? { ...q, ...updated } : q))
    setAnswer({ ...answer, [id]: '' })
  }

  async function handleReject(id: string) {
    await api.admin.questions.reject(id)
    setItems(items.filter((q) => q.id !== id))
  }

  return (
    <div>
      <div className="flex items-center gap-4 mb-4">
        <h1 className="text-xl font-bold text-gray-900 font-bn">প্রশ্নসমূহ</h1>
        <div className="flex gap-2">
          <button onClick={() => setPending(true)}
            className={`px-3 py-1 rounded-lg text-sm font-bn ${pending ? 'bg-brand-700 text-white' : 'bg-gray-100 text-gray-600'}`}>
            অপেক্ষমাণ
          </button>
          <button onClick={() => setPending(false)}
            className={`px-3 py-1 rounded-lg text-sm font-bn ${!pending ? 'bg-brand-700 text-white' : 'bg-gray-100 text-gray-600'}`}>
            সব
          </button>
        </div>
      </div>

      <div className="space-y-4">
        {items.map((q) => (
          <div key={q.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
            <div className="flex items-center gap-2 mb-2">
              <span className={`text-xs px-2 py-0.5 rounded font-bn ${
                q.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                q.status === 'ANSWERED' ? 'bg-green-100 text-green-700' :
                'bg-gray-100 text-gray-500'
              }`}>{q.status === 'PENDING' ? 'অপেক্ষমাণ' : q.status === 'ANSWERED' ? 'উত্তর দেওয়া' : 'বাতিল'}</span>
              <span className="text-xs text-gray-400 font-bn">{q.category}</span>
              {q.submitterName && <span className="text-xs text-gray-500 font-bn">— {q.submitterName}</span>}
            </div>
            <p className="text-gray-900 font-bn">{q.body}</p>

            {q.status === 'ANSWERED' && q.answer && (
              <div className="mt-3 bg-green-50 rounded-lg p-3">
                <p className="text-sm text-gray-700 font-bn">{q.answer}</p>
              </div>
            )}

            {q.status === 'PENDING' && (
              <div className="mt-3 space-y-2">
                <textarea
                  value={answer[q.id] ?? ''}
                  onChange={(e) => setAnswer({ ...answer, [q.id]: e.target.value })}
                  placeholder="উত্তর লিখুন..."
                  rows={3}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm font-bn resize-none focus:ring-2 focus:ring-brand-600"
                />
                <div className="flex gap-2">
                  <button
                    onClick={() => handleAnswer(q.id)}
                    disabled={!answer[q.id]?.trim()}
                    className="bg-brand-700 text-white px-3 py-1.5 rounded-lg text-sm font-bn disabled:opacity-50"
                  >
                    উত্তর দিন
                  </button>
                  <button
                    onClick={() => handleReject(q.id)}
                    className="bg-gray-100 text-gray-600 px-3 py-1.5 rounded-lg text-sm font-bn hover:bg-red-50 hover:text-red-600"
                  >
                    বাতিল করুন
                  </button>
                </div>
              </div>
            )}
          </div>
        ))}
        {items.length === 0 && (
          <p className="text-center text-gray-500 py-8 font-bn">কোনো প্রশ্ন নেই।</p>
        )}
      </div>
    </div>
  )
}

import { useEffect, useState } from 'react'
import { api, type BayahRequest } from '../../../lib/api'

const STATUS_LABELS: Record<string, string> = {
  NEW: 'নতুন', CONTACTED: 'যোগাযোগ হয়েছে', COMPLETED: 'সম্পন্ন', DECLINED: 'প্রত্যাখ্যাত'
}

export default function AdminBayah() {
  const [items, setItems] = useState<BayahRequest[]>([])

  useEffect(() => { api.admin.bayah.list().then(setItems) }, [])

  async function updateStatus(id: string, status: string) {
    const updated = await api.admin.bayah.updateStatus(id, status)
    setItems(items.map((b) => b.id === id ? { ...b, ...updated } : b))
  }

  return (
    <div>
      <h1 className="text-xl font-bold text-gray-900 font-bn mb-4">বাইয়াতের আবেদন</h1>
      <div className="space-y-3">
        {items.map((b) => (
          <div key={b.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
            <div className="flex items-center justify-between mb-2">
              <div>
                <span className="font-semibold font-bn text-gray-900">{b.name}</span>
                {b.district && <span className="text-gray-500 font-bn text-sm ml-2">({b.district})</span>}
              </div>
              <span className={`text-xs px-2 py-0.5 rounded font-bn ${
                b.status === 'NEW' ? 'bg-blue-100 text-blue-700' :
                b.status === 'CONTACTED' ? 'bg-yellow-100 text-yellow-700' :
                b.status === 'COMPLETED' ? 'bg-green-100 text-green-700' :
                'bg-gray-100 text-gray-500'
              }`}>{STATUS_LABELS[b.status]}</span>
            </div>
            {b.phone && <p className="text-sm text-gray-600 font-bn">📞 {b.phone}</p>}
            {b.note && <p className="text-sm text-gray-600 font-bn mt-1">{b.note}</p>}
            <div className="flex gap-2 mt-3">
              {(['NEW', 'CONTACTED', 'COMPLETED', 'DECLINED'] as const).map((s) => (
                <button
                  key={s}
                  onClick={() => updateStatus(b.id, s)}
                  className={`px-2 py-1 rounded text-xs font-bn transition-colors ${
                    b.status === s ? 'bg-brand-700 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                  }`}
                >
                  {STATUS_LABELS[s]}
                </button>
              ))}
            </div>
          </div>
        ))}
        {items.length === 0 && <p className="text-center text-gray-500 py-8 font-bn">কোনো আবেদন নেই।</p>}
      </div>
    </div>
  )
}

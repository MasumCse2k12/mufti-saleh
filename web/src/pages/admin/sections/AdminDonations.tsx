import { useEffect, useState } from 'react'
import { api } from '../../../lib/api'

interface Donation {
  id: string
  donorName: string | null
  amount: number
  purpose: string
  note: string | null
  createdAt: string
}

const PURPOSE_LABELS: Record<string, string> = {
  SADAQAH_JARIYAH: 'সদকায়ে জারিয়া', LILLAH: 'লিল্লাহ',
  BOOKS: 'কিতাব', MADRASA_FUND: 'মাদরাসা ফান্ড', OTHER: 'অন্যান্য'
}

export default function AdminDonations() {
  const [items, setItems] = useState<Donation[]>([])
  const [showForm, setShowForm] = useState(false)
  const [donorName, setDonorName] = useState('')
  const [amount, setAmount] = useState('')
  const [purpose, setPurpose] = useState('LILLAH')
  const [note, setNote] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    api.admin.donations.list().then((data) => setItems(data as unknown as Donation[]))
  }, [])

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    try {
      const created = await api.admin.donations.record({
        donorName: donorName || undefined, amount: parseFloat(amount), purpose, note: note || undefined
      })
      setItems([created as unknown as Donation, ...items])
      setDonorName(''); setAmount(''); setNote(''); setShowForm(false)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-bold text-gray-900 font-bn">ডোনেশন রেকর্ড</h1>
        <button onClick={() => setShowForm(!showForm)}
          className="bg-brand-700 text-white px-4 py-2 rounded-lg text-sm font-bn hover:bg-brand-800">
          + নতুন
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleCreate} className="bg-white rounded-xl p-4 shadow-sm mb-4 space-y-3">
          <input type="text" placeholder="দাতার নাম" value={donorName} onChange={(e) => setDonorName(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn focus:ring-2 focus:ring-brand-600" />
          <input type="number" placeholder="পরিমাণ (টাকা) *" value={amount} onChange={(e) => setAmount(e.target.value)} required min="1" step="0.01"
            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-brand-600" />
          <select value={purpose} onChange={(e) => setPurpose(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn">
            {Object.entries(PURPOSE_LABELS).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
          </select>
          <input type="text" placeholder="নোট" value={note} onChange={(e) => setNote(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 font-bn" />
          <div className="flex gap-2">
            <button type="submit" disabled={loading}
              className="bg-brand-700 text-white px-4 py-2 rounded-lg text-sm font-bn disabled:opacity-50">সংরক্ষণ</button>
            <button type="button" onClick={() => setShowForm(false)}
              className="bg-gray-100 text-gray-700 px-4 py-2 rounded-lg text-sm font-bn">বাতিল</button>
          </div>
        </form>
      )}

      <div className="space-y-2">
        {items.map((d) => (
          <div key={d.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex items-center justify-between">
            <div>
              <p className="font-semibold text-gray-900 font-bn">{d.donorName ?? 'অজ্ঞাত'}</p>
              <p className="text-sm text-gray-500 font-bn">{PURPOSE_LABELS[d.purpose] ?? d.purpose}</p>
            </div>
            <div className="text-right">
              <p className="font-bold text-brand-700 font-bn">৳ {Number(d.amount).toLocaleString('bn-BD')}</p>
              <p className="text-xs text-gray-400">{new Date(d.createdAt).toLocaleDateString('bn-BD')}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

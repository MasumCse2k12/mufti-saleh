import { Link, Routes, Route, useNavigate } from 'react-router-dom'
import { api } from '../../lib/api'
import AdminAnnouncements from './sections/AdminAnnouncements'
import AdminQuestions from './sections/AdminQuestions'
import AdminBayah from './sections/AdminBayah'
import AdminMedia from './sections/AdminMedia'
import AdminMasail from './sections/AdminMasail'
import AdminSchedule from './sections/AdminSchedule'
import AdminDonations from './sections/AdminDonations'

const NAV = [
  { to: '/admin', label: '🏠 ড্যাশবোর্ড', exact: true },
  { to: '/admin/announcements', label: '📢 ঘোষণা' },
  { to: '/admin/questions', label: '❓ প্রশ্নসমূহ' },
  { to: '/admin/bayah', label: '🤝 বাইয়াত' },
  { to: '/admin/media', label: '▶️ মিডিয়া' },
  { to: '/admin/masail', label: '👩 মাসায়েল' },
  { to: '/admin/schedule', label: '📅 সময়সূচি' },
  { to: '/admin/donations', label: '💰 ডোনেশন' },
]

export default function AdminDashboard() {
  const navigate = useNavigate()

  function logout() {
    localStorage.removeItem('admin_token')
    navigate('/admin/login')
  }

  return (
    <div className="min-h-screen bg-gray-100 flex">
      {/* Sidebar */}
      <aside className="w-56 bg-brand-800 text-white flex-shrink-0 flex flex-col">
        <div className="p-4 border-b border-brand-700">
          <h2 className="font-semibold font-bn text-sm leading-tight">শায়খ আবু সালেহ</h2>
          <p className="text-white/60 text-xs font-bn">অ্যাডমিন প্যানেল</p>
        </div>
        <nav className="flex-1 p-2 space-y-1">
          {NAV.map((item) => (
            <Link
              key={item.to}
              to={item.to}
              className="block px-3 py-2 rounded-lg text-sm font-bn text-white/80 hover:text-white hover:bg-white/10 transition-colors"
            >
              {item.label}
            </Link>
          ))}
        </nav>
        <div className="p-4 border-t border-brand-700">
          <Link to="/" className="block text-xs text-white/60 hover:text-white font-bn mb-2">← সাইট দেখুন</Link>
          <button onClick={logout} className="text-xs text-white/60 hover:text-white font-bn">লগআউট</button>
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 overflow-auto p-6">
        <Routes>
          <Route index element={<AdminHome />} />
          <Route path="announcements" element={<AdminAnnouncements />} />
          <Route path="questions" element={<AdminQuestions />} />
          <Route path="bayah" element={<AdminBayah />} />
          <Route path="media" element={<AdminMedia />} />
          <Route path="masail" element={<AdminMasail />} />
          <Route path="schedule" element={<AdminSchedule />} />
          <Route path="donations" element={<AdminDonations />} />
        </Routes>
      </main>
    </div>
  )
}

function AdminHome() {
  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 font-bn mb-4">ড্যাশবোর্ড</h1>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {NAV.slice(1).map((item) => (
          <Link
            key={item.to}
            to={item.to}
            className="bg-white rounded-xl p-4 shadow-sm hover:shadow-md transition-shadow text-center font-bn text-gray-700 hover:text-brand-700"
          >
            {item.label}
          </Link>
        ))}
      </div>
    </div>
  )
}

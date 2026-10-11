import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Home from './pages/Home'
import Announcements from './pages/Announcements'
import QuestionSubmit from './pages/QuestionSubmit'
import QuestionBrowse from './pages/QuestionBrowse'
import Donation from './pages/Donation'
import Bayah from './pages/Bayah'
import Schedule from './pages/Schedule'
import Media from './pages/Media'
import WomensMasail from './pages/WomensMasail'
import AdminLogin from './pages/admin/AdminLogin'
import AdminDashboard from './pages/admin/AdminDashboard'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('admin_token')
  return token ? <>{children}</> : <Navigate to="/admin/login" replace />
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public */}
        <Route path="/" element={<Home />} />
        <Route path="/announcements" element={<Announcements />} />
        <Route path="/questions/submit" element={<QuestionSubmit />} />
        <Route path="/questions" element={<QuestionBrowse />} />
        <Route path="/donation" element={<Donation />} />
        <Route path="/bayah" element={<Bayah />} />
        <Route path="/schedule" element={<Schedule />} />
        <Route path="/media" element={<Media />} />
        <Route path="/womens-masail" element={<WomensMasail />} />

        {/* Admin */}
        <Route path="/admin/login" element={<AdminLogin />} />
        <Route
          path="/admin/*"
          element={
            <PrivateRoute>
              <AdminDashboard />
            </PrivateRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  )
}

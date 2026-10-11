import { Link } from 'react-router-dom'

export default function Header() {
  return (
    <header className="bg-brand-700 text-white">
      <div className="max-w-2xl mx-auto px-4 py-6">
        <div className="flex items-center gap-4">
          <div className="w-20 h-20 rounded-lg bg-white/10 flex-shrink-0 overflow-hidden">
            <img
              src="/shaykh-photo.jpg"
              alt="শায়খ আবু সালেহ মুহাম্মাদুল্লাহ"
              className="w-full h-full object-cover"
              onError={(e) => {
                e.currentTarget.style.display = 'none'
              }}
            />
          </div>
          <div>
            <Link to="/">
              <h1 className="text-2xl font-bold leading-tight font-bn">
                শায়খ আবু সালেহ মুহাম্মাদুল্লাহ
              </h1>
            </Link>
            <p className="text-white/80 text-sm mt-1 font-bn">মুফতি · শাইখুল হাদিস</p>
            <p className="text-white/70 text-xs mt-0.5 font-bn">
              কুরআন ও সুন্নাহর আলোকে দীনি দিকনির্দেশনা
            </p>
          </div>
        </div>
      </div>
    </header>
  )
}

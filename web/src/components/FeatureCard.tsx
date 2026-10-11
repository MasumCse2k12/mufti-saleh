import { Link } from 'react-router-dom'

interface FeatureCardProps {
  to: string
  icon: React.ReactNode
  title: string
  subtitle: string
}

export default function FeatureCard({ to, icon, title, subtitle }: FeatureCardProps) {
  return (
    <Link
      to={to}
      className="flex items-center gap-4 bg-white rounded-xl p-4 shadow-sm border border-gray-100
                 hover:shadow-md hover:border-brand-500/30 transition-all active:scale-[0.98]"
    >
      <div className="w-14 h-14 rounded-xl bg-brand-50 flex items-center justify-center flex-shrink-0 text-brand-700">
        {icon}
      </div>
      <div className="flex-1 min-w-0">
        <h2 className="font-semibold text-gray-900 font-bn leading-snug">{title}</h2>
        <p className="text-sm text-gray-500 mt-0.5 font-bn">{subtitle}</p>
      </div>
      <svg className="w-5 h-5 text-gray-400 flex-shrink-0 rotate-180" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
      </svg>
    </Link>
  )
}

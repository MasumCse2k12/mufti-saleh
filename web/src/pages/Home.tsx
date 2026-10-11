import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Header from '../components/Header'
import FeatureCard from '../components/FeatureCard'
import { api, type Announcement } from '../lib/api'

export default function Home() {
  const [announcement, setAnnouncement] = useState<Announcement | null>(null)

  useEffect(() => {
    api.announcements.list().then((list) => {
      const pinned = list.find((a) => a.pinned) ?? list[0] ?? null
      setAnnouncement(pinned)
    }).catch(() => {})
  }, [])

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />

      {/* Emergency announcement */}
      {announcement && (
        <div className="max-w-2xl mx-auto px-4 pt-4">
          <Link to="/announcements">
            <div className="bg-cream border border-amber-200 rounded-xl p-4 shadow-sm">
              <div className="flex items-center gap-2 mb-2">
                <span className="text-amber-600">↺</span>
                <span className="font-semibold text-amber-800 font-bn">জরুরি ঘোষণা</span>
              </div>
              <p className="text-amber-900 text-sm font-bn leading-relaxed line-clamp-3">
                {announcement.body}
              </p>
            </div>
          </Link>
        </div>
      )}

      {/* Feature cards */}
      <main className="max-w-2xl mx-auto px-4 py-4 space-y-3">
        <FeatureCard
          to="/questions/submit"
          icon={<svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" /></svg>}
          title="প্রশ্ন জমা দিন"
          subtitle="আপনার দীনি প্রশ্ন লিখে পাঠান"
        />

        <FeatureCard
          to="/questions"
          icon={<svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>}
          title="পূর্বের প্রশ্ন দেখুন"
          subtitle="বিষয়ভিত্তিক প্রশ্ন ও উত্তর পড়ুন"
        />

        <FeatureCard
          to="/donation"
          icon={<svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>}
          title="শায়খের মাদরাসায় ডোনেশন করুন"
          subtitle="সদকায়ে জারিয়া · লিল্লাহ ফান্ড · কিতাব ক্রয়"
        />

        <FeatureCard
          to="/bayah"
          icon={<svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 11.5V14m0-2.5v-6a1.5 1.5 0 113 0m-3 6a1.5 1.5 0 00-3 0v2a7.5 7.5 0 0015 0v-5a1.5 1.5 0 00-3 0m-6-3V11m0-5.5v-1a1.5 1.5 0 013 0v1m0 0V11m0-5.5a1.5 1.5 0 013 0v3m0 0V11" /></svg>}
          title="শায়খের কাছে বাইয়াত নিতে চাই"
          subtitle="বাইয়াতের উদ্দেশ্য ও প্রক্রিয়া জানুন"
        />

        <FeatureCard
          to="/schedule"
          icon={<svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>}
          title="সাক্ষাৎ ও অনুষ্ঠানের সময়সূচি"
          subtitle="দরস, মাহফিল ও সাক্ষাতের তারিখ"
        />

        <FeatureCard
          to="/media"
          icon={<svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>}
          title="শায়খের আলোচনা শুনতে চাই"
          subtitle="বয়ান, তাফসির ও ইসলাহি আলোচনা"
        />

        <FeatureCard
          to="/womens-masail"
          icon={<svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" /></svg>}
          title="মহিলাদের উদ্দেশ্যে বিশেষ মাসায়েল"
          subtitle="নারীদের প্রয়োজনীয় মাসআলা ও বিশেষ আলোচনা"
        />
      </main>
    </div>
  )
}

/**
 * API client — the single seam between the React app and the Spring Boot backend.
 * Every network call goes through here. During development, fixtures are served
 * from localStorage if the env var VITE_USE_FIXTURES=true; in production every
 * method calls the real API.
 */

const BASE = (import.meta.env.VITE_API_BASE_URL ?? '/api') + '/v1'

async function get<T>(path: string): Promise<T> {
  const token = localStorage.getItem('admin_token')
  const res = await fetch(`${BASE}${path}`, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  })
  if (!res.ok) throw await res.json()
  return res.json()
}

async function post<T>(path: string, body?: unknown): Promise<T> {
  const token = localStorage.getItem('admin_token')
  const res = await fetch(`${BASE}${path}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body != null ? JSON.stringify(body) : undefined,
  })
  if (!res.ok) throw await res.json()
  if (res.status === 204) return undefined as T
  return res.json()
}

async function patch<T>(path: string, body: unknown): Promise<T> {
  const token = localStorage.getItem('admin_token')
  const res = await fetch(`${BASE}${path}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify(body),
  })
  if (!res.ok) throw await res.json()
  return res.json()
}

async function del(path: string): Promise<void> {
  const token = localStorage.getItem('admin_token')
  const res = await fetch(`${BASE}${path}`, {
    method: 'DELETE',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  })
  if (!res.ok) throw await res.json()
}

// --- Types ---

export interface Announcement {
  id: string
  title: string
  body: string
  pinned: boolean
  publishedAt: string
  expiresAt: string | null
}

export interface Question {
  id: string
  body: string
  category: string
  answer: string | null
  answeredAt: string | null
}

export interface QuestionAdmin extends Question {
  status: 'PENDING' | 'ANSWERED' | 'REJECTED'
  submitterName: string | null
  submitterContact: string | null
  createdAt: string
}

export interface DonationSummary {
  byPurpose: Record<string, number>
  total: number
}

export interface BayahRequest {
  id: string
  name: string
  phone: string | null
  district: string | null
  note: string | null
  status: 'NEW' | 'CONTACTED' | 'COMPLETED' | 'DECLINED'
  adminNote: string | null
  createdAt: string
}

export interface ScheduleEvent {
  id: string
  title: string
  description: string | null
  eventAt: string
  location: string | null
  type: string
}

export interface MediaItem {
  id: string
  title: string
  description: string | null
  type: string
  youtubeId: string | null
  mediaUrl: string | null
  durationSeconds: number | null
  publishedAt: string
}

export interface Masail {
  id: string
  question: string
  answer: string
  category: string
  createdAt: string
}

export interface LoginResponse {
  accessToken: string
  expiresInSeconds: number
  admin: { id: string; username: string; displayName: string; role: string }
}

// --- Public API ---

export const api = {
  announcements: {
    list: (cursor?: string) =>
      get<Announcement[]>(`/announcements${cursor ? `?cursor=${cursor}` : ''}`),
  },

  questions: {
    list: (category?: string, cursor?: string) => {
      const params = new URLSearchParams()
      if (category) params.set('category', category)
      if (cursor) params.set('cursor', cursor)
      const q = params.toString()
      return get<Question[]>(`/questions${q ? `?${q}` : ''}`)
    },
    submit: (body: string, category: string, submitterName?: string, submitterContact?: string) =>
      post<Question>('/questions', { body, category, submitterName, submitterContact }),
  },

  donations: {
    summary: () => get<DonationSummary>('/donations/summary'),
  },

  bayah: {
    submit: (data: { name: string; phone?: string; district?: string; note?: string }) =>
      post<BayahRequest>('/bayah', data),
  },

  schedule: {
    upcoming: () => get<ScheduleEvent[]>('/schedule'),
  },

  media: {
    list: (type?: string, cursor?: string) => {
      const params = new URLSearchParams()
      if (type) params.set('type', type)
      if (cursor) params.set('cursor', cursor)
      const q = params.toString()
      return get<MediaItem[]>(`/media${q ? `?${q}` : ''}`)
    },
  },

  masail: {
    list: (category?: string) =>
      get<Masail[]>(`/masail${category ? `?category=${category}` : ''}`),
  },

  // --- Admin ---

  auth: {
    login: (username: string, password: string) =>
      post<LoginResponse>('/auth/login', { username, password }),
    me: () => get<LoginResponse['admin']>('/admin/auth/me'),
    logout: () => post('/auth/logout'),
  },

  admin: {
    announcements: {
      list: () => get<Announcement[]>('/admin/announcements'),
      create: (data: { title: string; body: string; pinned: boolean; publishedAt: string; expiresAt?: string }) =>
        post<Announcement>('/admin/announcements', data),
      update: (id: string, data: Partial<{ title: string; body: string; pinned: boolean; publishedAt: string; expiresAt: string }>) =>
        patch<Announcement>(`/admin/announcements/${id}`, data),
      delete: (id: string) => del(`/admin/announcements/${id}`),
    },

    questions: {
      list: (pending = false) => get<QuestionAdmin[]>(`/admin/questions?pending=${pending}`),
      answer: (id: string, answer: string) => post<QuestionAdmin>(`/admin/questions/${id}/answer`, { answer }),
      reject: (id: string) => post<void>(`/admin/questions/${id}/reject`),
      delete: (id: string) => del(`/admin/questions/${id}`),
    },

    donations: {
      list: () => get<BayahRequest[]>('/admin/donations'),
      record: (data: { donorName?: string; amount: number; purpose: string; note?: string }) =>
        post('/admin/donations', data),
    },

    bayah: {
      list: (status?: string) => get<BayahRequest[]>(`/admin/bayah${status ? `?status=${status}` : ''}`),
      updateStatus: (id: string, status: string, adminNote?: string) =>
        patch(`/admin/bayah/${id}/status`, { status, adminNote }),
    },

    schedule: {
      list: () => get<ScheduleEvent[]>('/admin/schedule'),
      create: (data: { title: string; description?: string; eventAt: string; location?: string; type?: string }) =>
        post<ScheduleEvent>('/admin/schedule', data),
      update: (id: string, data: Partial<{ title: string; description: string; eventAt: string; location: string; type: string }>) =>
        patch<ScheduleEvent>(`/admin/schedule/${id}`, data),
      delete: (id: string) => del(`/admin/schedule/${id}`),
    },

    media: {
      create: (data: { title: string; description?: string; type?: string; youtubeId?: string; mediaUrl?: string; durationSeconds?: number; publishedAt: string }) =>
        post<MediaItem>('/admin/media', data),
      update: (id: string, data: Partial<MediaItem>) =>
        patch<MediaItem>(`/admin/media/${id}`, data),
      delete: (id: string) => del(`/admin/media/${id}`),
    },

    masail: {
      list: () => get<Masail[]>('/admin/masail'),
      create: (data: { question: string; answer: string; category?: string; published?: boolean }) =>
        post<Masail>('/admin/masail', data),
      update: (id: string, data: Partial<{ question: string; answer: string; category: string; published: boolean }>) =>
        patch<Masail>(`/admin/masail/${id}`, data),
      delete: (id: string) => del(`/admin/masail/${id}`),
    },
  },
}

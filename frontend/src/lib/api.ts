import axios from 'axios'
import { isDemoMode } from './env'
import { mockApi } from './mockApi'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'https://jobflow-api.fly.dev',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Inject Clerk JWT before every request
if (!isDemoMode) {
  api.interceptors.request.use(async (config) => {
    const clerk = (window as any).Clerk
    if (clerk?.session) {
      const token = await clerk.session.getToken()
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
    }
    return config
  })
}

// ─── Applications ────────────────────────────────────────────

export const applicationApi = {
  list: isDemoMode
    ? mockApi.applicationApi.list
    : (params?: { stage?: string; page?: number; size?: number }) =>
      api.get('/applications', { params }).then((r) => r.data),

  getById: isDemoMode
    ? mockApi.applicationApi.getById
    : (id: string) =>
      api.get(`/applications/${id}`).then((r) => r.data),

  create: isDemoMode
    ? mockApi.applicationApi.create
    : (data: object) =>
      api.post('/applications', data).then((r) => r.data),

  update: isDemoMode
    ? mockApi.applicationApi.update
    : (id: string, data: object) =>
      api.patch(`/applications/${id}`, data).then((r) => r.data),

  delete: isDemoMode
    ? mockApi.applicationApi.delete
    : (id: string) =>
      api.delete(`/applications/${id}`),
}

// ─── Analytics ───────────────────────────────────────────────

export const analyticsApi = {
  byStage: isDemoMode
    ? mockApi.analyticsApi.byStage
    : () => api.get('/analytics/by-stage').then((r) => r.data),
}

// ─── Resumes ─────────────────────────────────────────────────

export const historyApi = {
  list: isDemoMode
    ? mockApi.historyApi.list
    : (params?: { type?: string; page?: number; size?: number }) =>
      api.get('/history', { params }).then((r) => r.data),

  delete: isDemoMode
    ? mockApi.historyApi.delete
    : (id: string) =>
      api.delete(`/history/${id}`).then((r) => r.data),
}

export const resumeApi = {
  optimize: isDemoMode
    ? mockApi.resumeApi.optimize
    : (formData: FormData) =>
      api.post('/resumes/optimize', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      }).then((r) => r.data),
}

// ─── Salary ──────────────────────────────────────────────────

export const salaryApi = {
  research: isDemoMode
    ? mockApi.salaryApi.research
    : (params: { jobTitle: string; company?: string; location?: string; applicationId?: string }) =>
      api.post('/salary/research', null, { params }).then((r) => r.data),
}

// ─── User sync ───────────────────────────────────────────────

export const userApi = {
  sync: isDemoMode
    ? mockApi.userApi.sync
    : (email: string) =>
      api.post('/users/sync', null, { params: { email } }),
}

// ─── Profile ─────────────────────────────────────────────────

export const profileApi = {
  get: isDemoMode
    ? mockApi.profileApi.get
    : () => api.get('/profile').then((r) => r.data),
  update: isDemoMode
    ? mockApi.profileApi.update
    : (data: object) => api.put('/profile', data).then((r) => r.data),
}

// ─── Interview Prep ───────────────────────────────────────────

export const interviewApi = {
  generate: isDemoMode
    ? mockApi.interviewApi.generate
    : (data: object) => api.post('/interview-prep', data).then((r) => r.data),
  list: isDemoMode
    ? mockApi.interviewApi.list
    : () => api.get('/interview-prep').then((r) => r.data),
  getById: isDemoMode
    ? mockApi.interviewApi.getById
    : (id: string) => api.get(`/interview-prep/${id}`).then((r) => r.data),
}

export default api

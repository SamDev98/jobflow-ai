import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Inject Clerk JWT before every request
api.interceptors.request.use(async (config) => {
  // window.__clerk is set by ClerkProvider
  const clerk = (window as any).__clerk
  if (clerk?.session) {
    const token = await clerk.session.getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
  }
  return config
})

// ─── Applications ────────────────────────────────────────────

export const applicationApi = {
  list: (params?: { stage?: string; page?: number; size?: number }) =>
    api.get('/applications', { params }).then((r) => r.data),

  getById: (id: string) =>
    api.get(`/applications/${id}`).then((r) => r.data),

  create: (data: object) =>
    api.post('/applications', data).then((r) => r.data),

  update: (id: string, data: object) =>
    api.patch(`/applications/${id}`, data).then((r) => r.data),

  delete: (id: string) =>
    api.delete(`/applications/${id}`),
}

// ─── Analytics ───────────────────────────────────────────────

export const analyticsApi = {
  byStage: () => api.get('/analytics/by-stage').then((r) => r.data),
}

// ─── Resumes ─────────────────────────────────────────────────

export const resumeApi = {
  optimize: (formData: FormData) =>
    api.post('/resumes/optimize', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then((r) => r.data),
}

// ─── Salary ──────────────────────────────────────────────────

export const salaryApi = {
  research: (params: { jobTitle: string; company?: string; location?: string; applicationId?: string }) =>
    api.post('/salary/research', null, { params }).then((r) => r.data),
}

// ─── User sync ───────────────────────────────────────────────

export const userApi = {
  sync: (email: string) =>
    api.post('/users/sync', null, { params: { email } }),
}

// ─── Profile ─────────────────────────────────────────────────

export const profileApi = {
  get: () => api.get('/profile').then((r) => r.data),
  update: (data: object) => api.put('/profile', data).then((r) => r.data),
}

// ─── Interview Prep ───────────────────────────────────────────

export const interviewApi = {
  generate: (data: object) => api.post('/interview-prep', data).then((r) => r.data),
  list: () => api.get('/interview-prep').then((r) => r.data),
  getById: (id: string) => api.get(`/interview-prep/${id}`).then((r) => r.data),
}

export default api

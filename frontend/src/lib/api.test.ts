import { beforeEach, describe, expect, it, vi } from 'vitest'

const mocks = vi.hoisted(() => {
  const requestUse = vi.fn()
  const instance = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    patch: vi.fn(),
    delete: vi.fn(),
    interceptors: {
      request: { use: requestUse },
    },
  }

  return {
    instance,
    create: vi.fn(() => instance),
    requestUse,
  }
})

vi.mock('axios', () => ({
  default: {
    create: mocks.create,
  },
}))

describe('api clients', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('applicationApi list and create call correct endpoints', async () => {
    const { applicationApi } = await import('./api')

    expect(mocks.create).toHaveBeenCalledTimes(1)
    expect(mocks.requestUse).toHaveBeenCalledTimes(1)

    mocks.instance.get.mockResolvedValue({ data: { content: [] } })
    mocks.instance.post.mockResolvedValue({ data: { id: '1' } })

    await applicationApi.list({ stage: 'APPLIED', size: 20 })
    await applicationApi.create({ company: 'Acme', role: 'Backend' })

    expect(mocks.instance.get).toHaveBeenCalledWith('/applications', {
      params: { stage: 'APPLIED', size: 20 },
    })
    expect(mocks.instance.post).toHaveBeenCalledWith('/applications', {
      company: 'Acme',
      role: 'Backend',
    })
  })

  it('interview and profile apis call expected endpoints', async () => {
    const { interviewApi, profileApi } = await import('./api')

    mocks.instance.get.mockResolvedValue({ data: [] })
    mocks.instance.put.mockResolvedValue({ data: { yearsExperience: 5 } })

    await interviewApi.list()
    await profileApi.update({ yearsExperience: 5 })

    expect(mocks.instance.get).toHaveBeenCalledWith('/interview-prep')
    expect(mocks.instance.put).toHaveBeenCalledWith('/profile', { yearsExperience: 5 })
  })

  it('salary and resume apis send expected payload', async () => {
    const { salaryApi, resumeApi } = await import('./api')

    mocks.instance.post.mockResolvedValue({ data: {} })
    const formData = new FormData()
    formData.append('jd', 'Backend Engineer')

    await salaryApi.research({ jobTitle: 'Backend Engineer', location: 'Remote' })
    await resumeApi.optimize(formData)

    expect(mocks.instance.post).toHaveBeenNthCalledWith(1, '/salary/research', null, {
      params: { jobTitle: 'Backend Engineer', location: 'Remote' },
    })
    expect(mocks.instance.post).toHaveBeenNthCalledWith(2, '/resumes/optimize', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  })
})

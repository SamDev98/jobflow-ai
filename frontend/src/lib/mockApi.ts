import type {
  AnalyticsByStage,
  Application,
  CreateApplicationPayload,
  GenerateInterviewPrepPayload,
  HistoryItem,
  InterviewPrep,
  OptimizedResume,
  Page,
  SalaryRange,
  UpdateApplicationPayload,
  UserProfile,
} from '@/types'

const now = new Date().toISOString()

let applications: Application[] = [
  {
    id: 'app-1',
    company: 'Nubank',
    role: 'Backend Engineer',
    stage: 'TECHNICAL',
    techStack: ['Java', 'Spring Boot', 'PostgreSQL'],
    awaitingResponse: true,
    deadline: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString(),
    createdAt: now,
    updatedAt: now,
  },
  {
    id: 'app-2',
    company: 'Mercado Livre',
    role: 'Software Engineer',
    stage: 'SCREENING',
    techStack: ['Kotlin', 'Kafka'],
    awaitingResponse: false,
    deadline: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000).toISOString(),
    createdAt: now,
    updatedAt: now,
  },
  {
    id: 'app-3',
    company: 'iFood',
    role: 'Senior Backend Engineer',
    stage: 'APPLIED',
    awaitingResponse: true,
    createdAt: now,
    updatedAt: now,
  },
  {
    id: 'app-4',
    company: 'Stone',
    role: 'Platform Engineer',
    stage: 'ONSITE',
    awaitingResponse: false,
    createdAt: now,
    updatedAt: now,
  },
  {
    id: 'app-5',
    company: 'PicPay',
    role: 'Java Engineer',
    stage: 'OFFER',
    awaitingResponse: false,
    createdAt: now,
    updatedAt: now,
  },
]

let historyItems: HistoryItem[] = [
  {
    id: 'history-1',
    type: 'RESUME_OPTIMIZATION',
    title: 'Resume optimized for Backend Engineer role',
    content: 'Added measurable impact and reordered skills for ATS relevance.',
    createdAt: now,
  },
  {
    id: 'history-2',
    type: 'SALARY_RESEARCH',
    title: 'Salary benchmark for Senior Backend Engineer',
    content: 'Compared BR market ranges with remote LATAM opportunities.',
    createdAt: now,
  },
]

let profile: UserProfile = {
  yearsExperience: 5,
  techStack: ['Java', 'Spring Boot', 'React', 'PostgreSQL'],
  location: 'São Paulo, BR',
  workMode: 'Remote',
  salaryMinUsd: 80000,
}

let interviewPreps: InterviewPrep[] = [
  {
    id: 'prep-1',
    createdAt: now,
    questions: [
      {
        question: 'How would you design idempotent payment processing?',
        difficulty: 'medium',
        answerOutline: 'Use idempotency keys, unique constraints, and retry-safe handlers.',
      },
      {
        question: 'Explain trade-offs of eventual consistency in microservices.',
        difficulty: 'hard',
        answerOutline: 'Discuss availability, stale reads, compensating transactions, and UX impact.',
      },
    ],
  },
]

function deepClone<T>(data: T): T {
  return JSON.parse(JSON.stringify(data))
}

function withLatency<T>(data: T, ms = 180): Promise<T> {
  return new Promise((resolve) => {
    setTimeout(() => resolve(deepClone(data)), ms)
  })
}

function getStageAnalytics(): AnalyticsByStage {
  const base: AnalyticsByStage = {
    APPLIED: 0,
    SCREENING: 0,
    TECHNICAL: 0,
    ONSITE: 0,
    OFFER: 0,
    REJECTED: 0,
  }

  applications.forEach((app) => {
    base[app.stage] += 1
  })

  return base
}

function paginate<T>(items: T[], page = 0, size = 20): Page<T> {
  const start = page * size
  const content = items.slice(start, start + size)
  return {
    content,
    totalElements: items.length,
    totalPages: Math.max(1, Math.ceil(items.length / size)),
    number: page,
    size,
  }
}

function createInterviewQuestions(payload: GenerateInterviewPrepPayload) {
  const role = payload.jobTitle
  return [
    {
      question: `What architecture decisions would you make in your first 90 days as ${role}?`,
      difficulty: 'medium' as const,
      answerOutline: 'Prioritize reliability bottlenecks, observability, and incremental delivery.',
    },
    {
      question: `How would you evaluate technical debt in a ${role} context?`,
      difficulty: 'easy' as const,
      answerOutline: 'Use risk, business impact, and effort to rank debt remediation backlog.',
    },
    {
      question: `Describe a hard trade-off you would make for scale in a ${role} role.`,
      difficulty: 'hard' as const,
      answerOutline: 'Balance consistency, latency, and operational complexity with concrete metrics.',
    },
  ]
}

export const mockApi = {
  applicationApi: {
    list: (params?: { stage?: string; page?: number; size?: number }) => {
      const filtered = params?.stage
        ? applications.filter((app) => app.stage === params.stage)
        : applications

      return withLatency(paginate(filtered, params?.page ?? 0, params?.size ?? 100))
    },
    getById: async (id: string) => {
      const app = applications.find((item) => item.id === id)
      if (!app) {
        throw new Error('Application not found')
      }
      return withLatency(app)
    },
    create: async (data: CreateApplicationPayload) => {
      const created: Application = {
        id: `app-${Date.now()}`,
        company: data.company,
        role: data.role,
        jobDescription: data.jobDescription,
        jdUrl: data.jdUrl,
        techStack: data.techStack,
        stage: data.stage ?? 'APPLIED',
        salaryRangeLowUsd: data.salaryRangeLowUsd,
        salaryRangeHighUsd: data.salaryRangeHighUsd,
        deadline: data.deadline,
        notes: data.notes,
        awaitingResponse: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      }

      applications = [created, ...applications]
      return withLatency(created)
    },
    update: async (id: string, data: UpdateApplicationPayload) => {
      const index = applications.findIndex((item) => item.id === id)
      if (index < 0) {
        throw new Error('Application not found')
      }

      const updated: Application = {
        ...applications[index],
        ...data,
        updatedAt: new Date().toISOString(),
      }
      applications[index] = updated

      return withLatency(updated)
    },
    delete: async (id: string) => {
      applications = applications.filter((item) => item.id !== id)
      return withLatency(undefined)
    },
  },

  analyticsApi: {
    byStage: () => withLatency(getStageAnalytics()),
  },

  historyApi: {
    list: (params?: { type?: string; page?: number; size?: number }) => {
      const filtered = params?.type
        ? historyItems.filter((item) => item.type === params.type)
        : historyItems

      return withLatency(paginate(filtered, params?.page ?? 0, params?.size ?? 20))
    },
    delete: async (id: string) => {
      historyItems = historyItems.filter((item) => item.id !== id)
      return withLatency({ success: true })
    },
  },

  resumeApi: {
    optimize: async (_formData: FormData): Promise<OptimizedResume> => {
      const response: OptimizedResume = {
        resumeId: `resume-${Date.now()}`,
        atsScore: 87,
        docxUrl: '#',
        pdfUrl: '#',
        optimizedContent: 'Summary\nSenior backend engineer with strong system design background...\n\nExperience\n- Increased API reliability from 99.2% to 99.95%\n- Reduced endpoint latency by 35% through query/index tuning',
        skillsReordered: ['Java', 'Spring Boot', 'PostgreSQL', 'Redis', 'React'],
        experienceChanges: [
          {
            role: 'Backend Engineer',
            originalBullet: 'Worked on APIs.',
            optimizedBullet: 'Designed and maintained high-throughput APIs serving 2M+ monthly requests.',
            keywordsAdded: ['high-throughput', 'APIs', 'scalability'],
          },
        ],
      }

      historyItems = [
        {
          id: `history-${Date.now()}`,
          type: 'RESUME_OPTIMIZATION',
          title: 'Resume optimization completed',
          content: 'Generated ATS-optimized resume in demo mode.',
          createdAt: new Date().toISOString(),
        },
        ...historyItems,
      ]

      return withLatency(response, 350)
    },
  },

  salaryApi: {
    research: async (params: { jobTitle: string; company?: string; location?: string; applicationId?: string }): Promise<SalaryRange> => {
      const normalizedRole = params.jobTitle.toLowerCase()
      const baseMid = normalizedRole.includes('senior') ? 130000 : 95000
      const response: SalaryRange = {
        researchId: `salary-${Date.now()}`,
        rangeLowUsd: Math.round(baseMid * 0.8),
        rangeMidUsd: baseMid,
        rangeHighUsd: Math.round(baseMid * 1.25),
        confidenceScore: 8,
        reasoning: `Benchmark simulated for ${params.jobTitle}${params.location ? ` in ${params.location}` : ''}.`,
      }

      historyItems = [
        {
          id: `history-${Date.now()}`,
          type: 'SALARY_RESEARCH',
          title: `Salary research for ${params.jobTitle}`,
          content: response.reasoning,
          createdAt: new Date().toISOString(),
        },
        ...historyItems,
      ]

      return withLatency(response, 280)
    },
  },

  userApi: {
    sync: (_email: string) => withLatency({ success: true }),
  },

  profileApi: {
    get: () => withLatency(profile),
    update: async (data: Partial<UserProfile>) => {
      profile = { ...profile, ...data }
      return withLatency(profile)
    },
  },

  interviewApi: {
    list: () => withLatency(interviewPreps),
    generate: async (data: GenerateInterviewPrepPayload) => {
      const prep: InterviewPrep = {
        id: `prep-${Date.now()}`,
        applicationId: data.applicationId,
        createdAt: new Date().toISOString(),
        questions: createInterviewQuestions(data),
      }

      interviewPreps = [prep, ...interviewPreps]
      historyItems = [
        {
          id: `history-${Date.now()}`,
          type: 'INTERVIEW_PREP',
          title: `Interview prep for ${data.jobTitle}`,
          content: `Generated ${prep.questions.length} interview questions in demo mode.`,
          createdAt: prep.createdAt,
        },
        ...historyItems,
      ]

      return withLatency(prep, 240)
    },
    getById: async (id: string) => {
      const prep = interviewPreps.find((item) => item.id === id)
      if (!prep) {
        throw new Error('Interview prep not found')
      }
      return withLatency(prep)
    },
  },
}

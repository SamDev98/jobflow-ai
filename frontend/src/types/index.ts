export type Stage =
  | 'APPLIED'
  | 'SCREENING'
  | 'TECHNICAL'
  | 'ONSITE'
  | 'OFFER'
  | 'REJECTED'

export type Tier = 'FREE' | 'PRO'

export type HistoryType =
  | 'RESUME_OPTIMIZATION'
  | 'INTERVIEW_PREP'
  | 'SALARY_RESEARCH'
  | 'APPLICATION_AUTO_FILL'

export interface HistoryItem {
  id: string
  type: HistoryType
  title: string
  content: string
  metadata?: string
  createdAt: string
}

export interface Application {
  id: string
  company: string
  role: string
  jobDescription?: string
  jdUrl?: string
  techStack?: string[]
  stage: Stage
  salaryRangeLowUsd?: number
  salaryRangeHighUsd?: number
  deadline?: string
  awaitingResponse: boolean
  interviewDatetime?: string
  notes?: string
  createdAt: string
  updatedAt: string
}

export interface CreateApplicationPayload {
  company: string
  role: string
  jobDescription?: string
  jdUrl?: string
  techStack?: string[]
  stage?: Stage
  salaryRangeLowUsd?: number
  salaryRangeHighUsd?: number
  deadline?: string
  notes?: string
}

export interface UpdateApplicationPayload {
  company?: string
  role?: string
  jobDescription?: string
  jdUrl?: string
  techStack?: string[]
  stage?: Stage
  salaryRangeLowUsd?: number
  salaryRangeHighUsd?: number
  deadline?: string
  awaitingResponse?: boolean
  notes?: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface ExperienceChange {
  role: string
  originalBullet: string
  optimizedBullet: string
  keywordsAdded: string[]
}

export interface OptimizedResume {
  resumeId: string
  docxUrl: string
  pdfUrl: string
  atsScore: number
  optimizedContent?: string
  skillsReordered: string[]
  experienceChanges: ExperienceChange[]
}

export interface SalaryRange {
  researchId: string
  rangeLowUsd: number
  rangeMidUsd: number
  rangeHighUsd: number
  reasoning: string
  confidenceScore: number
}

export interface AnalyticsByStage {
  APPLIED: number
  SCREENING: number
  TECHNICAL: number
  ONSITE: number
  OFFER: number
  REJECTED: number
}

export interface UserProfile {
  yearsExperience?: number
  techStack?: string[]
  location?: string
  workMode?: string
  salaryMinUsd?: number
}

export interface UpdateProfilePayload {
  yearsExperience?: number
  techStack?: string[]
  location?: string
  workMode?: string
  salaryMinUsd?: number
}

export interface Question {
  question: string
  answerOutline: string
  difficulty: 'easy' | 'medium' | 'hard'
}

export interface InterviewPrep {
  id: string
  applicationId?: string
  questions: Question[]
  createdAt: string
}

export interface GenerateInterviewPrepPayload {
  applicationId?: string
  jobTitle: string
  company?: string
  jobDescription?: string
  techStack?: string[]
}

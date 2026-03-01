import { useMutation } from '@tanstack/react-query'
import { salaryApi } from '@/lib/api'
import type { SalaryRange } from '@/types'

interface SalaryResearchParams {
  jobTitle: string
  company?: string
  location?: string
  applicationId?: string
}

export function useSalaryResearch() {
  return useMutation<SalaryRange, Error, SalaryResearchParams>({
    mutationFn: (params) => salaryApi.research(params),
  })
}

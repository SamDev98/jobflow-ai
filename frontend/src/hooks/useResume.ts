import { useMutation } from '@tanstack/react-query'
import { resumeApi } from '@/lib/api'
import type { OptimizedResume } from '@/types'

export function useOptimizeResume() {
  return useMutation<OptimizedResume, Error, FormData>({
    mutationFn: (formData: FormData) => resumeApi.optimize(formData),
  })
}

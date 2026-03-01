import { useQuery } from '@tanstack/react-query'
import { analyticsApi } from '@/lib/api'
import type { AnalyticsByStage } from '@/types'

export function useAnalyticsByStage() {
  return useQuery<AnalyticsByStage>({
    queryKey: ['analytics', 'by-stage'],
    queryFn: () => analyticsApi.byStage(),
    staleTime: 1000 * 60 * 5, // 5 minutes
  })
}

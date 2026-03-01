import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { historyApi } from '@/lib/api'
import type { HistoryType } from '@/types'

export function useHistory(type?: HistoryType, page = 0, size = 20) {
  return useQuery({
    queryKey: ['history', type, page, size],
    queryFn: () => historyApi.list({ type, page, size }),
  })
}

export function useDeleteHistory() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: string) => historyApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['history'] })
    },
  })
}

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { applicationApi } from '@/lib/api'
import type {
  Application,
  CreateApplicationPayload,
  Page,
  Stage,
  UpdateApplicationPayload,
} from '@/types'

const QUERY_KEY = 'applications'

export function useApplications(stage?: Stage) {
  return useQuery<Page<Application>>({
    queryKey: [QUERY_KEY, stage],
    queryFn: () => applicationApi.list({ stage, size: 100 }),
  })
}

export function useApplication(id: string) {
  return useQuery<Application>({
    queryKey: [QUERY_KEY, id],
    queryFn: () => applicationApi.getById(id),
    enabled: !!id,
  })
}

export function useCreateApplication() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateApplicationPayload) => applicationApi.create(data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: [QUERY_KEY] }),
  })
}

export function useUpdateApplication() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateApplicationPayload }) =>
      applicationApi.update(id, data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: [QUERY_KEY] }),
  })
}

export function useDeleteApplication() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: string) => applicationApi.delete(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: [QUERY_KEY] }),
  })
}

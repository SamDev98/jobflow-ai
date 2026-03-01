import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { profileApi } from '../lib/api'
import type { UpdateProfilePayload, UserProfile } from '../types'

export function useProfile() {
  return useQuery<UserProfile>({
    queryKey: ['profile'],
    queryFn: profileApi.get,
  })
}

export function useUpdateProfile() {
  const queryClient = useQueryClient()
  return useMutation<UserProfile, Error, UpdateProfilePayload>({
    mutationFn: (data) => profileApi.update(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['profile'] })
    },
  })
}

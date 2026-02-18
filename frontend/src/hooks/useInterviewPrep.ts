import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { interviewApi } from '../lib/api'
import type { GenerateInterviewPrepPayload, InterviewPrep } from '../types'

export function useInterviewPreps() {
  return useQuery<InterviewPrep[]>({
    queryKey: ['interview-preps'],
    queryFn: interviewApi.list,
  })
}

export function useGeneratePrep() {
  const queryClient = useQueryClient()
  return useMutation<InterviewPrep, Error, GenerateInterviewPrepPayload>({
    mutationFn: (data) => interviewApi.generate(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['interview-preps'] })
    },
  })
}

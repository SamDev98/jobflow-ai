import { renderHook, act } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { useSalaryResearch } from './useSalary';
import { createWrapper } from '@/test/queryClientWrapper';

const mocks = vi.hoisted(() => ({
  researchMock: vi.fn(),
}));

vi.mock('@/lib/api', () => ({
  salaryApi: {
    research: mocks.researchMock,
  },
}));

describe('useSalaryResearch', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('calls salary research API', async () => {
    mocks.researchMock.mockResolvedValue({
      rangeLowUsd: 100000,
      rangeMidUsd: 120000,
      rangeHighUsd: 150000,
    });

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useSalaryResearch(), {
      wrapper: Wrapper,
    });

    await act(async () => {
      await result.current.mutateAsync({
        jobTitle: 'Backend Engineer',
        location: 'Remote',
      });
    });

    expect(mocks.researchMock).toHaveBeenCalledWith({
      jobTitle: 'Backend Engineer',
      location: 'Remote',
    });
  });
});

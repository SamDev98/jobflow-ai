import { renderHook, act } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { useOptimizeResume } from './useResume';
import { createWrapper } from '@/test/queryClientWrapper';

const mocks = vi.hoisted(() => ({
  optimizeMock: vi.fn(),
}));

vi.mock('@/lib/api', () => ({
  resumeApi: {
    optimize: mocks.optimizeMock,
  },
}));

describe('useOptimizeResume', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('calls optimize endpoint with FormData', async () => {
    mocks.optimizeMock.mockResolvedValue({ resumeId: 'r-1', atsScore: 90 });

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useOptimizeResume(), {
      wrapper: Wrapper,
    });

    const formData = new FormData();
    formData.append('jd', 'Backend Engineer JD');

    await act(async () => {
      await result.current.mutateAsync(formData);
    });

    expect(mocks.optimizeMock).toHaveBeenCalledWith(formData);
  });
});

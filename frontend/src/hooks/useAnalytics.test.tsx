import { renderHook, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { useAnalyticsByStage } from './useAnalytics';
import { createWrapper } from '@/test/queryClientWrapper';

const mocks = vi.hoisted(() => ({
  byStageMock: vi.fn(),
}));

vi.mock('@/lib/api', () => ({
  analyticsApi: {
    byStage: mocks.byStageMock,
  },
}));

describe('useAnalyticsByStage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('loads analytics by stage', async () => {
    mocks.byStageMock.mockResolvedValue({
      APPLIED: 1,
      SCREENING: 2,
      TECHNICAL: 0,
      ONSITE: 0,
      OFFER: 0,
      REJECTED: 1,
    });

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useAnalyticsByStage(), {
      wrapper: Wrapper,
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data?.SCREENING).toBe(2);
  });
});

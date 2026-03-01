import { renderHook, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { useHistory } from './useHistory';
import { createWrapper } from '@/test/queryClientWrapper';

const mocks = vi.hoisted(() => ({
  listMock: vi.fn(),
}));

vi.mock('@/lib/api', () => ({
  historyApi: {
    list: mocks.listMock,
  },
}));

describe('useHistory', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('loads history items', async () => {
    mocks.listMock.mockResolvedValue({
      content: [
        { id: '1', title: 'Action 1', type: 'OTHER' },
        { id: '2', title: 'Action 2', type: 'INTERVIEW_PREP' },
      ],
      totalElements: 2,
    });

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useHistory(), {
      wrapper: Wrapper,
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data?.content).toHaveLength(2);
    expect(mocks.listMock).toHaveBeenCalled();
  });
});

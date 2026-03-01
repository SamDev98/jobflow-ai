import { renderHook, waitFor, act } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { useInterviewPreps, useGeneratePrep } from './useInterviewPrep';
import { createWrapper } from '@/test/queryClientWrapper';

const mocks = vi.hoisted(() => ({
  listMock: vi.fn(),
  generateMock: vi.fn(),
}));

vi.mock('../lib/api', () => ({
  interviewApi: {
    list: mocks.listMock,
    generate: mocks.generateMock,
  },
}));

describe('useInterviewPrep hooks', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('lists interview preps', async () => {
    mocks.listMock.mockResolvedValue([{ id: 'prep-1', questions: [] }]);

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useInterviewPreps(), {
      wrapper: Wrapper,
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data?.length).toBe(1);
  });

  it('generates interview prep', async () => {
    mocks.generateMock.mockResolvedValue({ id: 'prep-2', questions: [] });

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useGeneratePrep(), {
      wrapper: Wrapper,
    });

    await act(async () => {
      await result.current.mutateAsync({ jobTitle: 'Backend Engineer' });
    });

    expect(mocks.generateMock).toHaveBeenCalledWith({
      jobTitle: 'Backend Engineer',
    });
  });
});

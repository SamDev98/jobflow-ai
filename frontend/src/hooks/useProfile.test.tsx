import { renderHook, waitFor, act } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { useProfile, useUpdateProfile } from './useProfile';
import { createWrapper } from '@/test/queryClientWrapper';

const mocks = vi.hoisted(() => ({
  getMock: vi.fn(),
  updateMock: vi.fn(),
}));

vi.mock('../lib/api', () => ({
  profileApi: {
    get: mocks.getMock,
    update: mocks.updateMock,
  },
}));

describe('useProfile', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('loads profile data', async () => {
    mocks.getMock.mockResolvedValue({ yearsExperience: 4, location: 'Remote' });

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useProfile(), { wrapper: Wrapper });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data?.yearsExperience).toBe(4);
  });

  it('updates profile data', async () => {
    mocks.updateMock.mockResolvedValue({
      yearsExperience: 5,
      location: 'Remote',
    });

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useUpdateProfile(), {
      wrapper: Wrapper,
    });

    await act(async () => {
      await result.current.mutateAsync({ yearsExperience: 5 });
    });

    expect(mocks.updateMock).toHaveBeenCalledWith({ yearsExperience: 5 });
  });
});

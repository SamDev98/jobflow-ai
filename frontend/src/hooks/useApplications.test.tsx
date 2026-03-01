import { renderHook, waitFor, act } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import {
  useApplications,
  useApplication,
  useCreateApplication,
  useUpdateApplication,
  useDeleteApplication,
} from './useApplications';
import { createWrapper } from '@/test/queryClientWrapper';

const mocks = vi.hoisted(() => ({
  listMock: vi.fn(),
  getByIdMock: vi.fn(),
  createMock: vi.fn(),
  updateMock: vi.fn(),
  deleteMock: vi.fn(),
}));

vi.mock('@/lib/api', () => ({
  applicationApi: {
    list: mocks.listMock,
    getById: mocks.getByIdMock,
    create: mocks.createMock,
    update: mocks.updateMock,
    delete: mocks.deleteMock,
  },
}));

describe('useApplications', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('loads applications with stage filter', async () => {
    mocks.listMock.mockResolvedValue({
      content: [],
      totalElements: 0,
      totalPages: 0,
      number: 0,
      size: 100,
    });

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useApplications('APPLIED'), {
      wrapper: Wrapper,
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(mocks.listMock).toHaveBeenCalledWith({
      stage: 'APPLIED',
      size: 100,
    });
  });

  it('creates application through mutation', async () => {
    mocks.createMock.mockResolvedValue({
      id: '1',
      company: 'Acme',
      role: 'Backend',
      stage: 'APPLIED',
    });

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useCreateApplication(), {
      wrapper: Wrapper,
    });

    await act(async () => {
      await result.current.mutateAsync({ company: 'Acme', role: 'Backend' });
    });

    expect(mocks.createMock).toHaveBeenCalledWith({
      company: 'Acme',
      role: 'Backend',
    });
  });

  it('loads one application by id', async () => {
    mocks.getByIdMock.mockResolvedValue({
      id: 'a1',
      company: 'Acme',
      role: 'Backend',
      stage: 'APPLIED',
    });

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useApplication('a1'), {
      wrapper: Wrapper,
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(mocks.getByIdMock).toHaveBeenCalledWith('a1');
  });

  it('updates application through mutation', async () => {
    mocks.updateMock.mockResolvedValue({ id: 'a1' });

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useUpdateApplication(), {
      wrapper: Wrapper,
    });

    await act(async () => {
      await result.current.mutateAsync({
        id: 'a1',
        data: { stage: 'SCREENING' },
      });
    });

    expect(mocks.updateMock).toHaveBeenCalledWith('a1', { stage: 'SCREENING' });
  });

  it('deletes application through mutation', async () => {
    mocks.deleteMock.mockResolvedValue(undefined);

    const { Wrapper } = createWrapper();
    const { result } = renderHook(() => useDeleteApplication(), {
      wrapper: Wrapper,
    });

    await act(async () => {
      await result.current.mutateAsync('a1');
    });

    expect(mocks.deleteMock).toHaveBeenCalledWith('a1');
  });
});

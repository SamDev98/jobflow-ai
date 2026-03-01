import React, { useState } from 'react';
import * as Dialog from '@radix-ui/react-dialog';
import { Plus, X } from 'lucide-react';
import { useCreateApplication } from '@/hooks/useApplications';
import { Stage } from '@/types';

export function AddApplicationDialog() {
  const [open, setOpen] = useState(false);
  const createApplication = useCreateApplication();
  const [formData, setFormData] = useState({
    company: '',
    role: '',
    jdUrl: '',
    salaryRangeLowUsd: '',
    salaryRangeHighUsd: '',
    stage: 'APPLIED' as Stage,
    notes: '',
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    await createApplication.mutateAsync({
      ...formData,
      salaryRangeLowUsd: formData.salaryRangeLowUsd
        ? Number(formData.salaryRangeLowUsd)
        : undefined,
      salaryRangeHighUsd: formData.salaryRangeHighUsd
        ? Number(formData.salaryRangeHighUsd)
        : undefined,
    });

    setOpen(false);
    setFormData({
      company: '',
      role: '',
      jdUrl: '',
      salaryRangeLowUsd: '',
      salaryRangeHighUsd: '',
      stage: 'APPLIED' as Stage,
      notes: '',
    });
  };

  return (
    <Dialog.Root open={open} onOpenChange={setOpen}>
      <Dialog.Trigger asChild>
        <button className='flex items-center gap-2 bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-lg font-medium transition-colors'>
          <Plus className='w-4 h-4' />
          Add Application
        </button>
      </Dialog.Trigger>
      <Dialog.Portal>
        <Dialog.Overlay className='fixed inset-0 bg-black/50 backdrop-blur-sm z-50 animate-in fade-in duration-200' />
        <Dialog.Content className='fixed left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 w-full max-w-lg bg-card border border-border p-6 rounded-xl shadow-2xl z-50 animate-in zoom-in-95 duration-200'>
          <div className='flex items-center justify-between mb-6'>
            <Dialog.Title className='text-xl font-bold text-foreground'>
              New Application
            </Dialog.Title>
            <Dialog.Close asChild>
              <button className='text-muted-foreground hover:text-foreground transition-colors'>
                <X className='w-5 h-5' />
              </button>
            </Dialog.Close>
          </div>

          <form onSubmit={handleSubmit} className='space-y-4'>
            <div className='grid grid-cols-2 gap-4'>
              <div className='space-y-1.5'>
                <label className='text-sm font-medium text-muted-foreground'>
                  Company *
                </label>
                <input
                  required
                  placeholder='Google, Meta...'
                  className='w-full bg-muted border border-border text-foreground rounded-md px-3 py-2 focus:ring-2 focus:ring-primary outline-none'
                  value={formData.company}
                  onChange={(e) =>
                    setFormData({ ...formData, company: e.target.value })
                  }
                />
              </div>
              <div className='space-y-1.5'>
                <label className='text-sm font-medium text-muted-foreground'>
                  Role *
                </label>
                <input
                  required
                  placeholder='Software Engineer...'
                  className='w-full bg-muted border border-border text-foreground rounded-md px-3 py-2 focus:ring-2 focus:ring-primary outline-none'
                  value={formData.role}
                  onChange={(e) =>
                    setFormData({ ...formData, role: e.target.value })
                  }
                />
              </div>
            </div>

            <div className='space-y-1.5'>
              <label className='text-sm font-medium text-muted-foreground'>
                Job URL
              </label>
              <input
                type='url'
                placeholder='https://linkedin.com/jobs/...'
                className='w-full bg-muted border border-border text-foreground rounded-md px-3 py-2 focus:ring-2 focus:ring-primary outline-none'
                value={formData.jdUrl}
                onChange={(e) =>
                  setFormData({ ...formData, jdUrl: e.target.value })
                }
              />
            </div>

            <div className='grid grid-cols-2 gap-4'>
              <div className='space-y-1.5'>
                <label className='text-sm font-medium text-muted-foreground'>
                  Salary Low (USD)
                </label>
                <input
                  type='number'
                  placeholder='80000'
                  className='w-full bg-muted border border-border text-foreground rounded-md px-3 py-2 focus:ring-2 focus:ring-primary outline-none'
                  value={formData.salaryRangeLowUsd}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      salaryRangeLowUsd: e.target.value,
                    })
                  }
                />
              </div>
              <div className='space-y-1.5'>
                <label className='text-sm font-medium text-muted-foreground'>
                  Salary High (USD)
                </label>
                <input
                  type='number'
                  placeholder='120000'
                  className='w-full bg-muted border border-border text-foreground rounded-md px-3 py-2 focus:ring-2 focus:ring-primary outline-none'
                  value={formData.salaryRangeHighUsd}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      salaryRangeHighUsd: e.target.value,
                    })
                  }
                />
              </div>
            </div>

            <div className='space-y-1.5'>
              <label className='text-sm font-medium text-muted-foreground'>
                Current Stage
              </label>
              <select
                className='w-full bg-muted border border-border text-foreground rounded-md px-3 py-2 focus:ring-2 focus:ring-primary outline-none transition-all cursor-pointer'
                value={formData.stage}
                onChange={(e) =>
                  setFormData({ ...formData, stage: e.target.value as Stage })
                }
              >
                <option value='APPLIED'>Applied</option>
                <option value='SCREENING'>Screening</option>
                <option value='TECHNICAL'>Technical Interview</option>
                <option value='ONSITE'>Onsite</option>
                <option value='OFFER'>Offer</option>
                <option value='REJECTED'>Rejected</option>
              </select>
            </div>

            <div className='space-y-1.5'>
              <label className='text-sm font-medium text-muted-foreground'>
                Notes
              </label>
              <textarea
                placeholder='Internal referrals, etc.'
                className='w-full bg-muted border border-border text-foreground rounded-md px-3 py-2 focus:ring-2 focus:ring-primary outline-none h-24 resize-none transition-all'
                value={formData.notes}
                onChange={(e) =>
                  setFormData({ ...formData, notes: e.target.value })
                }
              />
            </div>

            <div className='flex gap-3 pt-4'>
              <Dialog.Close asChild>
                <button
                  type='button'
                  className='flex-1 bg-muted hover:bg-muted/80 text-foreground px-4 py-2 rounded-lg font-medium transition-colors'
                >
                  Cancel
                </button>
              </Dialog.Close>
              <button
                type='submit'
                disabled={createApplication.isPending}
                className='flex-1 bg-primary hover:bg-primary/90 disabled:opacity-50 text-primary-foreground px-4 py-2 rounded-lg font-medium transition-colors'
              >
                {createApplication.isPending ? 'Saving...' : 'Save Application'}
              </button>
            </div>
          </form>
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  );
}

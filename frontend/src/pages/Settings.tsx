import { useEffect, useState } from 'react';
import { UserProfile } from '@clerk/clerk-react';
import { Loader2, Save } from 'lucide-react';
import { useProfile, useUpdateProfile } from '@/hooks/useProfile';
import { isDemoMode } from '@/lib/env';

const WORK_MODE_OPTIONS = ['Remote', 'Hybrid', 'On-site'];

export function Settings() {
  const { data: profile, isLoading } = useProfile();
  const { mutate: save, isPending, isSuccess } = useUpdateProfile();

  const [yearsExperience, setYearsExperience] = useState<number | ''>('');
  const [techStack, setTechStack] = useState('');
  const [location, setLocation] = useState('');
  const [workMode, setWorkMode] = useState('');
  const [salaryMinUsd, setSalaryMinUsd] = useState<number | ''>('');

  useEffect(() => {
    if (profile) {
      setYearsExperience(profile.yearsExperience ?? '');
      setTechStack(profile.techStack?.join(', ') ?? '');
      setLocation(profile.location ?? '');
      setWorkMode(profile.workMode ?? '');
      setSalaryMinUsd(profile.salaryMinUsd ?? '');
    }
  }, [profile]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    save({
      yearsExperience:
        yearsExperience !== '' ? Number(yearsExperience) : undefined,
      techStack: techStack
        ? techStack
            .split(',')
            .map((s) => s.trim())
            .filter(Boolean)
        : undefined,
      location: location || undefined,
      workMode: workMode || undefined,
      salaryMinUsd: salaryMinUsd !== '' ? Number(salaryMinUsd) : undefined,
    });
  };

  return (
    <div className='space-y-8'>
      <h1 className='text-2xl font-bold'>Settings</h1>

      {/* Job Profile */}
      <section className='space-y-4'>
        <div>
          <h2 className='text-lg font-semibold'>Job Profile</h2>
          <p className='text-sm text-muted-foreground'>
            Used by Resume Optimizer and Salary Research to personalise AI
            suggestions.
          </p>
        </div>

        {isLoading ? (
          <div className='flex items-center gap-2 text-sm text-muted-foreground'>
            <Loader2 size={16} className='animate-spin' /> Loading profile…
          </div>
        ) : (
          <form onSubmit={handleSubmit} className='space-y-4 max-w-lg'>
            <div className='grid grid-cols-2 gap-4'>
              <div className='space-y-1'>
                <label className='text-sm font-medium'>
                  Years of Experience
                </label>
                <input
                  type='number'
                  min={0}
                  max={50}
                  value={yearsExperience}
                  onChange={(e) =>
                    setYearsExperience(
                      e.target.value === '' ? '' : Number(e.target.value),
                    )
                  }
                  placeholder='5'
                  className='w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary'
                />
              </div>
              <div className='space-y-1'>
                <label className='text-sm font-medium'>
                  Min Salary (USD/yr)
                </label>
                <input
                  type='number'
                  min={0}
                  value={salaryMinUsd}
                  onChange={(e) =>
                    setSalaryMinUsd(
                      e.target.value === '' ? '' : Number(e.target.value),
                    )
                  }
                  placeholder='80000'
                  className='w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary'
                />
              </div>
            </div>

            <div className='space-y-1'>
              <label className='text-sm font-medium'>
                Tech Stack (comma-separated)
              </label>
              <input
                value={techStack}
                onChange={(e) => setTechStack(e.target.value)}
                placeholder='Java, Spring Boot, React, PostgreSQL'
                className='w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary'
              />
            </div>

            <div className='grid grid-cols-2 gap-4'>
              <div className='space-y-1'>
                <label className='text-sm font-medium'>Location</label>
                <input
                  value={location}
                  onChange={(e) => setLocation(e.target.value)}
                  placeholder='São Paulo, BR'
                  className='w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary'
                />
              </div>
              <div className='space-y-1'>
                <label className='text-sm font-medium'>Work Mode</label>
                <select
                  value={workMode}
                  onChange={(e) => setWorkMode(e.target.value)}
                  className='w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary bg-background'
                >
                  <option value=''>Select…</option>
                  {WORK_MODE_OPTIONS.map((o) => (
                    <option key={o} value={o}>
                      {o}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <button
              type='submit'
              disabled={isPending}
              className='flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm font-medium disabled:opacity-50 hover:bg-primary/90 transition-colors'
            >
              {isPending ? (
                <Loader2 size={16} className='animate-spin' />
              ) : (
                <Save size={16} />
              )}
              {isPending ? 'Saving…' : isSuccess ? 'Saved!' : 'Save Profile'}
            </button>
          </form>
        )}
      </section>

      {/* Clerk account management */}
      <section className='space-y-4'>
        <div>
          <h2 className='text-lg font-semibold'>Account</h2>
          <p className='text-sm text-muted-foreground'>
            {isDemoMode
              ? 'Demo mode enabled. Account management is available in self-host mode with Clerk configured.'
              : 'Manage your Clerk account, email, and security settings.'}
          </p>
        </div>
        {isDemoMode ? (
          <div className='rounded-md border border-dashed p-4 text-sm text-muted-foreground'>
            Account controls are hidden in demo mode.
          </div>
        ) : (
          <UserProfile />
        )}
      </section>
    </div>
  );
}

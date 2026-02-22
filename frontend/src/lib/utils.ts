import { type ClassValue, clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatCurrency(usd: number | undefined): string {
  if (usd === undefined) return '—'
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 0,
  }).format(usd)
}

export function formatDate(iso: string | undefined): string {
  if (!iso) return '—'
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short' }).format(new Date(iso))
}

export function formatDateTime(iso: string | undefined): string {
  if (!iso) return '—'
  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(iso))
}

export const STAGE_LABELS: Record<string, string> = {
  APPLIED: 'Applied',
  SCREENING: 'Screening',
  TECHNICAL: 'Technical',
  ONSITE: 'On-site',
  OFFER: 'Offer',
  REJECTED: 'Rejected',
}

export const STAGE_COLORS: Record<string, string> = {
  APPLIED: 'bg-blue-500/10 text-blue-400 border border-blue-500/20',
  SCREENING: 'bg-yellow-500/10 text-yellow-400 border border-yellow-500/20',
  TECHNICAL: 'bg-purple-500/10 text-purple-400 border border-purple-500/20',
  ONSITE: 'bg-orange-500/10 text-orange-400 border border-orange-500/20',
  OFFER: 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20',
  REJECTED: 'bg-rose-500/10 text-rose-400 border border-rose-500/20',
}

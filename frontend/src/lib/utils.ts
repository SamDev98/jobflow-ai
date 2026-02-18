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
  APPLIED: 'bg-blue-100 text-blue-800',
  SCREENING: 'bg-yellow-100 text-yellow-800',
  TECHNICAL: 'bg-purple-100 text-purple-800',
  ONSITE: 'bg-orange-100 text-orange-800',
  OFFER: 'bg-green-100 text-green-800',
  REJECTED: 'bg-red-100 text-red-800',
}

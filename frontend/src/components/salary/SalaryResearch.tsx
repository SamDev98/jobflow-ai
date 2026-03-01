import { useState } from 'react'
import { Loader2, TrendingUp } from 'lucide-react'
import { useSalaryResearch } from '@/hooks/useSalary'
import { formatCurrency } from '@/lib/utils'
import type { SalaryRange } from '@/types'

export function SalaryResearch() {
  const [jobTitle, setJobTitle] = useState('')
  const [company, setCompany] = useState('')
  const [location, setLocation] = useState('')
  const [result, setResult] = useState<SalaryRange | null>(null)
  const research = useSalaryResearch()

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!jobTitle.trim()) return
    const data = await research.mutateAsync({ jobTitle, company, location })
    setResult(data)
  }

  return (
    <div className="space-y-6 max-w-xl">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Job Title *</label>
          <input
            value={jobTitle}
            onChange={(e) => setJobTitle(e.target.value)}
            placeholder="e.g. Senior Backend Engineer"
            className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-sm font-medium mb-1">Company</label>
            <input
              value={company}
              onChange={(e) => setCompany(e.target.value)}
              placeholder="Optional"
              className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Location</label>
            <input
              value={location}
              onChange={(e) => setLocation(e.target.value)}
              placeholder="e.g. Brazil / Remote"
              className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
            />
          </div>
        </div>

        <button
          type="submit"
          disabled={research.isPending || !jobTitle.trim()}
          className="flex items-center gap-2 rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground disabled:opacity-50"
        >
          {research.isPending ? (
            <Loader2 size={16} className="animate-spin" />
          ) : (
            <TrendingUp size={16} />
          )}
          {research.isPending ? 'Researching...' : 'Research Salary'}
        </button>
      </form>

      {result && (
        <div className="rounded-xl border p-5 space-y-4">
          <h3 className="font-semibold">Salary Range (USD/year)</h3>

          <div className="grid grid-cols-3 gap-4 text-center">
            <div>
              <p className="text-xs text-muted-foreground mb-1">Low</p>
              <p className="text-xl font-bold text-red-500">{formatCurrency(result.rangeLowUsd)}</p>
            </div>
            <div>
              <p className="text-xs text-muted-foreground mb-1">Mid</p>
              <p className="text-xl font-bold text-primary">{formatCurrency(result.rangeMidUsd)}</p>
            </div>
            <div>
              <p className="text-xs text-muted-foreground mb-1">High</p>
              <p className="text-xl font-bold text-green-500">{formatCurrency(result.rangeHighUsd)}</p>
            </div>
          </div>

          <div>
            <p className="text-xs font-medium mb-1">Reasoning</p>
            <p className="text-xs text-muted-foreground leading-relaxed">{result.reasoning}</p>
          </div>

          <p className="text-xs text-muted-foreground">
            Confidence: {result.confidenceScore}/10
          </p>
        </div>
      )}
    </div>
  )
}

import { useState } from 'react'
import { BrainCircuit, ChevronDown, ChevronUp, Loader2 } from 'lucide-react'
import { useGeneratePrep, useInterviewPreps } from '@/hooks/useInterviewPrep'
import { cn } from '@/lib/utils'
import type { Question } from '@/types'

const DIFFICULTY_COLORS: Record<string, string> = {
  easy: 'bg-green-100 text-green-800',
  medium: 'bg-yellow-100 text-yellow-800',
  hard: 'bg-red-100 text-red-800',
}

function QuestionCard({ q, index }: { q: Question; index: number }) {
  const [open, setOpen] = useState(false)
  return (
    <div className="border rounded-lg overflow-hidden">
      <button
        onClick={() => setOpen((v) => !v)}
        className="w-full flex items-start justify-between p-4 text-left hover:bg-muted/30 transition-colors"
      >
        <div className="flex items-start gap-3">
          <span className="text-xs text-muted-foreground mt-0.5 w-5 shrink-0">{index + 1}.</span>
          <span className="font-medium text-sm">{q.question}</span>
        </div>
        <div className="flex items-center gap-2 ml-4 shrink-0">
          <span
            className={cn(
              'text-xs px-2 py-0.5 rounded-full font-medium capitalize',
              DIFFICULTY_COLORS[q.difficulty] ?? 'bg-gray-100 text-gray-700',
            )}
          >
            {q.difficulty}
          </span>
          {open ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
        </div>
      </button>
      {open && (
        <div className="px-4 pb-4 pt-0 border-t bg-muted/20">
          <p className="text-sm text-muted-foreground leading-relaxed">{q.answerOutline}</p>
        </div>
      )}
    </div>
  )
}

export function QuizInterface() {
  const { data: history } = useInterviewPreps()
  const { mutate: generate, isPending, data: result } = useGeneratePrep()

  const [jobTitle, setJobTitle] = useState('')
  const [company, setCompany] = useState('')
  const [jobDescription, setJobDescription] = useState('')
  const [techStack, setTechStack] = useState('')

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!jobTitle.trim()) return
    generate({
      jobTitle,
      company: company || undefined,
      jobDescription: jobDescription || undefined,
      techStack: techStack ? techStack.split(',').map((s) => s.trim()).filter(Boolean) : undefined,
    })
  }

  const displayed = result ?? (history && history.length > 0 ? history[0] : null)

  return (
    <div className="space-y-6">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-1">
            <label className="text-sm font-medium">Job Title *</label>
            <input
              required
              value={jobTitle}
              onChange={(e) => setJobTitle(e.target.value)}
              placeholder="Senior Backend Engineer"
              className="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          <div className="space-y-1">
            <label className="text-sm font-medium">Company</label>
            <input
              value={company}
              onChange={(e) => setCompany(e.target.value)}
              placeholder="Nubank"
              className="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
        </div>

        <div className="space-y-1">
          <label className="text-sm font-medium">Tech Stack (comma-separated)</label>
          <input
            value={techStack}
            onChange={(e) => setTechStack(e.target.value)}
            placeholder="Java, Spring Boot, Kafka, PostgreSQL"
            className="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>

        <div className="space-y-1">
          <label className="text-sm font-medium">Job Description</label>
          <textarea
            rows={4}
            value={jobDescription}
            onChange={(e) => setJobDescription(e.target.value)}
            placeholder="Paste the job description here..."
            className="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary resize-none"
          />
        </div>

        <button
          type="submit"
          disabled={isPending || !jobTitle.trim()}
          className="flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm font-medium disabled:opacity-50 hover:bg-primary/90 transition-colors"
        >
          {isPending ? (
            <Loader2 size={16} className="animate-spin" />
          ) : (
            <BrainCircuit size={16} />
          )}
          {isPending ? 'Generating...' : 'Generate Questions'}
        </button>
      </form>

      {displayed && (
        <div className="space-y-3">
          <h3 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide">
            {displayed.questions.length} Questions
          </h3>
          {displayed.questions.map((q, i) => (
            <QuestionCard key={i} q={q} index={i} />
          ))}
        </div>
      )}
    </div>
  )
}

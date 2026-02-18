import { useRef, useState } from 'react'
import { Upload, FileText, Loader2 } from 'lucide-react'
import { useOptimizeResume } from '@/hooks/useResume'
import { DiffViewer } from './DiffViewer'
import type { OptimizedResume } from '@/types'

export function ResumeOptimizer() {
  const [jd, setJd] = useState('')
  const [file, setFile] = useState<File | null>(null)
  const [result, setResult] = useState<OptimizedResume | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)
  const optimize = useOptimizeResume()

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const selected = e.target.files?.[0]
    if (selected) setFile(selected)
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!file || !jd.trim()) return

    const formData = new FormData()
    formData.append('resume', file)
    formData.append('jd', jd)

    const data = await optimize.mutateAsync(formData)
    setResult(data)
  }

  return (
    <div className="space-y-6 max-w-3xl">
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* File upload */}
        <div>
          <label className="block text-sm font-medium mb-1">Resume (.docx)</label>
          <div
            onClick={() => fileInputRef.current?.click()}
            className="flex cursor-pointer items-center gap-3 rounded-lg border-2 border-dashed border-border p-4 hover:border-primary transition-colors"
          >
            <FileText size={20} className="text-muted-foreground" />
            <span className="text-sm text-muted-foreground">
              {file ? file.name : 'Click to select .docx file'}
            </span>
            <Upload size={16} className="ml-auto text-muted-foreground" />
          </div>
          <input
            ref={fileInputRef}
            type="file"
            accept=".docx"
            className="hidden"
            onChange={handleFileChange}
          />
        </div>

        {/* JD textarea */}
        <div>
          <label className="block text-sm font-medium mb-1">Job Description</label>
          <textarea
            value={jd}
            onChange={(e) => setJd(e.target.value)}
            placeholder="Paste the full job description here..."
            rows={8}
            className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm resize-none focus:outline-none focus:ring-2 focus:ring-ring"
          />
        </div>

        <button
          type="submit"
          disabled={optimize.isPending || !file || !jd.trim()}
          className="flex items-center gap-2 rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground disabled:opacity-50"
        >
          {optimize.isPending && <Loader2 size={16} className="animate-spin" />}
          {optimize.isPending ? 'Optimizing...' : 'Optimize Resume'}
        </button>
      </form>

      {/* Results */}
      {result && (
        <div className="space-y-4 rounded-xl border p-5">
          <div className="flex items-center justify-between">
            <h3 className="font-semibold">Optimization Results</h3>
            <span className="text-sm font-medium text-primary">ATS Score: {result.atsScore}%</span>
          </div>

          <div className="flex gap-3">
            <a
              href={result.docxUrl}
              target="_blank"
              rel="noreferrer"
              className="rounded-lg border px-3 py-1.5 text-xs font-medium hover:bg-muted"
            >
              Download .docx
            </a>
            <a
              href={result.pdfUrl}
              target="_blank"
              rel="noreferrer"
              className="rounded-lg border px-3 py-1.5 text-xs font-medium hover:bg-muted"
            >
              Download .pdf
            </a>
          </div>

          <DiffViewer changes={result.experienceChanges} />
        </div>
      )}
    </div>
  )
}

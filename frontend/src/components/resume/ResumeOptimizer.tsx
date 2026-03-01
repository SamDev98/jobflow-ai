import { useRef, useState } from 'react';
import { Upload, FileText, Loader2, FileCheck } from 'lucide-react';
import { useOptimizeResume } from '@/hooks/useResume';
import { DiffViewer } from './DiffViewer';
import type { OptimizedResume } from '@/types';

export function ResumeOptimizer() {
  const [jd, setJd] = useState('');
  const [baseFile, setBaseFile] = useState<File | null>(null);
  const [templateFile, setTemplateFile] = useState<File | null>(null);
  const [result, setResult] = useState<OptimizedResume | null>(null);

  const baseInputRef = useRef<HTMLInputElement>(null);
  const templateInputRef = useRef<HTMLInputElement>(null);
  const optimize = useOptimizeResume();

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!baseFile || !jd.trim()) return;

    const formData = new FormData();
    formData.append('resume', baseFile);
    if (templateFile) {
      formData.append('template', templateFile);
    }
    formData.append('jd', jd);

    const data = await optimize.mutateAsync(formData);
    setResult(data);
  }

  return (
    <div className='space-y-6 max-w-4xl mx-auto p-4'>
      <div className='bg-card border border-border rounded-xl p-6 shadow-xl'>
        <h2 className='text-xl font-bold text-foreground mb-6'>
          Resume Optimizer & ATS Aligner
        </h2>

        <form onSubmit={handleSubmit} className='space-y-6'>
          <div className='grid grid-cols-1 md:grid-cols-2 gap-6'>
            {/* Base Resume */}
            <div className='space-y-2'>
              <label className='block text-sm font-medium text-muted-foreground'>
                Base Resume (Your current info)
              </label>
              <div
                onClick={() => baseInputRef.current?.click()}
                className={`flex cursor-pointer items-center gap-3 rounded-lg border-2 border-dashed p-6 transition-all ${
                  baseFile
                    ? 'border-primary bg-primary/5'
                    : 'border-border hover:border-primary/50 bg-muted/50'
                }`}
              >
                <FileText
                  size={24}
                  className={
                    baseFile ? 'text-primary' : 'text-muted-foreground'
                  }
                />
                <div className='flex flex-col min-w-0'>
                  <span className='text-sm font-medium text-foreground truncate'>
                    {baseFile ? baseFile.name : 'Upload Base Resume'}
                  </span>
                  <span className='text-xs text-muted-foreground'>
                    Only .docx supported
                  </span>
                </div>
                {!baseFile && (
                  <Upload size={18} className='ml-auto text-muted-foreground' />
                )}
              </div>
              <input
                ref={baseInputRef}
                type='file'
                accept='.docx'
                className='hidden'
                onChange={(e) => setBaseFile(e.target.files?.[0] || null)}
              />
            </div>

            {/* Template Resume */}
            <div className='space-y-2'>
              <label className='block text-sm font-medium text-muted-foreground'>
                Target Template (Style reference)
              </label>
              <div
                onClick={() => templateInputRef.current?.click()}
                className={`flex cursor-pointer items-center gap-3 rounded-lg border-2 border-dashed p-6 transition-all ${
                  templateFile
                    ? 'border-primary bg-primary/5'
                    : 'border-border hover:border-primary/50 bg-muted/50'
                }`}
              >
                <FileCheck
                  size={24}
                  className={
                    templateFile ? 'text-primary' : 'text-muted-foreground'
                  }
                />
                <div className='flex flex-col min-w-0'>
                  <span className='text-sm font-medium text-foreground truncate'>
                    {templateFile ? templateFile.name : 'Upload Style Template'}
                  </span>
                  <span className='text-xs text-muted-foreground'>
                    Optional style reference
                  </span>
                </div>
                {!templateFile && (
                  <Upload size={18} className='ml-auto text-muted-foreground' />
                )}
              </div>
              <input
                ref={templateInputRef}
                type='file'
                accept='.docx'
                className='hidden'
                onChange={(e) => setTemplateFile(e.target.files?.[0] || null)}
              />
            </div>
          </div>

          {/* JD textarea */}
          <div className='space-y-2'>
            <label className='block text-sm font-medium text-muted-foreground'>
              Job Description
            </label>
            <textarea
              required
              value={jd}
              onChange={(e) => setJd(e.target.value)}
              placeholder='Paste the full job description here to align your experience...'
              rows={6}
              className='w-full rounded-lg border border-border bg-muted/50 px-4 py-3 text-sm text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary transition-all placeholder:text-muted-foreground/50'
            />
          </div>

          <button
            type='submit'
            disabled={optimize.isPending || !baseFile || !jd.trim()}
            className='w-full flex items-center justify-center gap-2 rounded-lg bg-primary hover:bg-primary/90 px-6 py-3 text-sm font-bold text-primary-foreground transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-lg shadow-primary/20'
          >
            {optimize.isPending ? (
              <Loader2 size={18} className='animate-spin' />
            ) : (
              <Upload size={18} />
            )}
            {optimize.isPending
              ? 'Optimizing your career...'
              : 'Generate AI Optimized Resume'}
          </button>
        </form>
      </div>

      {/* Results */}
      {result && (
        <div className='space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500'>
          <div className='grid grid-cols-1 md:grid-cols-3 gap-4'>
            <div className='bg-card border border-border p-5 rounded-xl flex flex-col items-center justify-center text-center'>
              <span className='text-xs font-medium text-muted-foreground uppercase tracking-wider mb-1'>
                ATS Score
              </span>
              <span className='text-3xl font-bold text-primary'>
                {result.atsScore}%
              </span>
            </div>
            <div className='md:col-span-2 bg-card border border-border p-5 rounded-xl flex items-center justify-center gap-4'>
              <a
                href={result.docxUrl}
                target='_blank'
                rel='noreferrer'
                className='flex-1 flex items-center justify-center gap-2 rounded-lg bg-muted hover:bg-muted/80 border border-border transition-colors px-4 py-3 text-sm font-medium text-foreground'
              >
                <FileText size={18} />
                Download Optimized .docx
              </a>
              <a
                href={result.pdfUrl}
                target='_blank'
                rel='noreferrer'
                className='flex-1 flex items-center justify-center gap-2 rounded-lg bg-muted hover:bg-muted/80 border border-border transition-colors px-4 py-3 text-sm font-medium text-foreground'
              >
                <FileCheck size={18} />
                Download PDF Preview
              </a>
            </div>
          </div>

          {result.optimizedContent && (
            <div className='bg-card border border-border rounded-xl overflow-hidden'>
              <div className='px-5 py-3 border-b border-border bg-muted/30 flex items-center justify-between'>
                <h3 className='text-sm font-bold text-muted-foreground'>
                  Generated Resume Content
                </h3>
                <span className='text-[10px] px-2 py-0.5 rounded bg-primary/10 text-primary border border-primary/20 font-mono'>
                  STYLE MATCHED
                </span>
              </div>
              <div className='p-6 overflow-auto max-h-[600px]'>
                <pre className='text-xs text-muted-foreground whitespace-pre-wrap font-sans leading-relaxed'>
                  {result.optimizedContent}
                </pre>
              </div>
            </div>
          )}

          <div className='bg-card border border-border rounded-xl p-5'>
            <h3 className='text-sm font-bold text-muted-foreground mb-4 px-1'>
              Specific Experience Changes
            </h3>
            <DiffViewer changes={result.experienceChanges} />
          </div>
        </div>
      )}
    </div>
  );
}

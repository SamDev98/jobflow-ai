import { useState } from 'react'
import { BrainCircuit, FileText, TrendingUp } from 'lucide-react'
import { ResumeOptimizer } from '@/components/resume/ResumeOptimizer'
import { SalaryResearch } from '@/components/salary/SalaryResearch'
import { QuizInterface } from '@/components/interview/QuizInterface'
import { cn } from '@/lib/utils'

type Tab = 'resume' | 'salary' | 'interview'

const tabs = [
  { id: 'resume' as Tab, label: 'Resume Optimizer', icon: FileText },
  { id: 'salary' as Tab, label: 'Salary Research', icon: TrendingUp },
  { id: 'interview' as Tab, label: 'Interview Prep', icon: BrainCircuit },
]

export function Tools() {
  const [activeTab, setActiveTab] = useState<Tab>('resume')

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Tools</h1>

      <div className="flex gap-2 border-b">
        {tabs.map(({ id, label, icon: Icon }) => (
          <button
            key={id}
            onClick={() => setActiveTab(id)}
            className={cn(
              'flex items-center gap-2 px-4 py-2 text-sm font-medium border-b-2 -mb-px transition-colors',
              activeTab === id
                ? 'border-primary text-primary'
                : 'border-transparent text-muted-foreground hover:text-foreground',
            )}
          >
            <Icon size={16} />
            {label}
          </button>
        ))}
      </div>

      {activeTab === 'resume' && <ResumeOptimizer />}
      {activeTab === 'salary' && <SalaryResearch />}
      {activeTab === 'interview' && <QuizInterface />}
    </div>
  )
}

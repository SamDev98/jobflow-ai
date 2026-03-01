import { useSortable } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { Building2, Calendar, DollarSign, Trash2 } from 'lucide-react'
import { cn, formatCurrency, formatDate, STAGE_COLORS } from '@/lib/utils'
import { useDeleteApplication } from '@/hooks/useApplications'
import type { Application } from '@/types'

interface Props {
  application: Application
}

export function ApplicationCard({ application }: Props) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } =
    useSortable({ id: application.id })

  const deleteApplication = useDeleteApplication()

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  }

  function handleDelete(e: React.MouseEvent) {
    e.stopPropagation()
    if (confirm(`Delete "${application.role}" at ${application.company}?`)) {
      deleteApplication.mutate(application.id)
    }
  }

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      className={cn(
        'rounded-lg border bg-card p-3 shadow-sm cursor-grab select-none',
        'hover:shadow-md transition-shadow',
        isDragging && 'opacity-50 shadow-lg',
      )}
    >
      <div className="mb-1 flex items-start justify-between gap-2">
        <span className="font-semibold text-sm leading-tight">{application.role}</span>
        <div className="flex items-center gap-1 shrink-0">
          <span
            className={cn(
              'rounded-full px-2 py-0.5 text-xs font-medium',
              STAGE_COLORS[application.stage],
            )}
          >
            {application.stage}
          </span>
          <button
            onPointerDown={(e) => e.stopPropagation()}
            onClick={handleDelete}
            disabled={deleteApplication.isPending}
            className="text-muted-foreground hover:text-destructive transition-colors p-0.5 rounded disabled:opacity-50"
            aria-label="Delete application"
          >
            <Trash2 size={14} />
          </button>
        </div>
      </div>

      <div className="flex items-center gap-1 text-xs text-muted-foreground mb-2">
        <Building2 size={12} />
        <span>{application.company}</span>
      </div>

      <div className="flex items-center justify-between text-xs text-muted-foreground">
        {application.salaryRangeLowUsd && (
          <span className="flex items-center gap-1">
            <DollarSign size={12} />
            {formatCurrency(application.salaryRangeLowUsd)}
            {application.salaryRangeHighUsd && ` – ${formatCurrency(application.salaryRangeHighUsd)}`}
          </span>
        )}
        {application.deadline && (
          <span className="flex items-center gap-1">
            <Calendar size={12} />
            {formatDate(application.deadline)}
          </span>
        )}
      </div>
    </div>
  )
}

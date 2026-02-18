import { useDroppable } from '@dnd-kit/core'
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable'
import { cn, STAGE_LABELS } from '@/lib/utils'
import { ApplicationCard } from './ApplicationCard'
import type { Application, Stage } from '@/types'

interface Props {
  stage: Stage
  applications: Application[]
}

export function StageColumn({ stage, applications }: Props) {
  const { setNodeRef, isOver } = useDroppable({ id: stage })

  return (
    <div className="flex w-64 shrink-0 flex-col gap-2">
      <div className="flex items-center justify-between px-1">
        <h3 className="text-sm font-semibold">{STAGE_LABELS[stage]}</h3>
        <span className="rounded-full bg-muted px-2 py-0.5 text-xs text-muted-foreground">
          {applications.length}
        </span>
      </div>

      <div
        ref={setNodeRef}
        className={cn(
          'flex min-h-[120px] flex-col gap-2 rounded-xl border-2 border-dashed p-2 transition-colors',
          isOver ? 'border-primary bg-primary/5' : 'border-border bg-muted/30',
        )}
      >
        <SortableContext
          items={applications.map((a) => a.id)}
          strategy={verticalListSortingStrategy}
        >
          {applications.map((app) => (
            <ApplicationCard key={app.id} application={app} />
          ))}
        </SortableContext>

        {applications.length === 0 && (
          <p className="py-4 text-center text-xs text-muted-foreground">Drop here</p>
        )}
      </div>
    </div>
  )
}

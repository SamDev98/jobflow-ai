import { DndContext, DragEndEvent, PointerSensor, useSensor, useSensors } from '@dnd-kit/core'
import { useApplications, useUpdateApplication } from '@/hooks/useApplications'
import { StageColumn } from './StageColumn'
import type { Stage } from '@/types'

const STAGES: Stage[] = ['APPLIED', 'SCREENING', 'TECHNICAL', 'ONSITE', 'OFFER', 'REJECTED']

export function KanbanBoard() {
  const { data, isLoading } = useApplications()
  const updateApplication = useUpdateApplication()

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: { distance: 5 },
    }),
  )

  const applications = data?.content ?? []

  function handleDragEnd(event: DragEndEvent) {
    const { active, over } = event
    if (!over || active.id === over.id) return

    const applicationId = active.id as string
    const newStage = over.id as Stage

    // Only update if dropped on a stage column
    if (STAGES.includes(newStage)) {
      updateApplication.mutate({
        id: applicationId,
        data: { stage: newStage },
      })
    }
  }

  if (isLoading) {
    return <div className="flex items-center justify-center h-64 text-muted-foreground">Loading...</div>
  }

  return (
    <DndContext sensors={sensors} onDragEnd={handleDragEnd}>
      <div className="flex gap-4 overflow-x-auto pb-4">
        {STAGES.map((stage) => (
          <StageColumn
            key={stage}
            stage={stage}
            applications={applications.filter((a) => a.stage === stage)}
          />
        ))}
      </div>
    </DndContext>
  )
}

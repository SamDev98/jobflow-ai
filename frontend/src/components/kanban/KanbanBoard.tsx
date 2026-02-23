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
    if (!over) return

    const applicationId = active.id as string
    const overId = over.id as string

    // Resolve the target stage: either a column id or the stage of the card being dropped onto
    let targetStage: Stage | undefined

    if (STAGES.includes(overId as Stage)) {
      targetStage = overId as Stage
    } else {
      // overId is another application's id — find which stage it belongs to
      const overApp = applications.find((a) => a.id === overId)
      targetStage = overApp?.stage
    }

    if (!targetStage) return

    // Find the current stage of the dragged application
    const activeApp = applications.find((a) => a.id === applicationId)
    if (!activeApp || activeApp.stage === targetStage) return

    updateApplication.mutate({
      id: applicationId,
      data: { stage: targetStage },
    })
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

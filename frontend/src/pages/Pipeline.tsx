import { KanbanBoard } from '@/components/kanban/KanbanBoard'

export function Pipeline() {
  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Pipeline</h1>
      <KanbanBoard />
    </div>
  )
}

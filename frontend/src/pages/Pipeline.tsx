import { KanbanBoard } from '@/components/kanban/KanbanBoard';
import { AddApplicationDialog } from '@/components/kanban/AddApplicationDialog';

export function Pipeline() {
  return (
    <div className='space-y-6'>
      <div className='flex items-center justify-between'>
        <h1 className='text-2xl font-bold text-white tracking-tight'>
          Pipeline
        </h1>
        <AddApplicationDialog />
      </div>
      <KanbanBoard />
    </div>
  );
}

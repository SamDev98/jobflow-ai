import { useHistory, useDeleteHistory } from '@/hooks/useHistory';
import {
  Clock,
  FileText,
  Briefcase,
  Search,
  Trash2,
  Calendar,
} from 'lucide-react';
import { format } from 'date-fns';
import type { HistoryType, HistoryItem } from '@/types';

const TYPE_ICONS: Record<HistoryType, any> = {
  RESUME_OPTIMIZATION: FileText,
  INTERVIEW_PREP: Calendar,
  SALARY_RESEARCH: Search,
  APPLICATION_AUTO_FILL: Briefcase,
};

export function History() {
  const { data, isLoading } = useHistory();
  const deleteHistory = useDeleteHistory();

  const items: HistoryItem[] = data?.content ?? [];

  if (isLoading) {
    return (
      <div className='flex items-center justify-center h-64'>
        <div className='animate-spin rounded-full h-8 w-8 border-b-2 border-primary' />
      </div>
    );
  }

  return (
    <div className='space-y-6'>
      <div className='flex items-center justify-between'>
        <h1 className='text-2xl font-bold text-foreground tracking-tight flex items-center gap-2'>
          <Clock className='w-6 h-6 text-primary' />
          History
        </h1>
      </div>

      <div className='grid gap-4'>
        {items.length === 0 ? (
          <div className='bg-card border border-border p-12 rounded-xl text-center'>
            <Clock className='w-12 h-12 text-muted-foreground mx-auto mb-4 opacity-20' />
            <p className='text-muted-foreground'>
              No history yet. Start using the tools to see logs here!
            </p>
          </div>
        ) : (
          items.map((item) => {
            const Icon = TYPE_ICONS[item.type] || Clock;
            return (
              <div
                key={item.id}
                className='group bg-card hover:bg-muted/50 border border-border p-4 rounded-xl transition-all flex items-start gap-4'
              >
                <div className='p-2 bg-primary/10 rounded-lg text-primary'>
                  <Icon className='w-5 h-5' />
                </div>

                <div className='flex-1 min-w-0'>
                  <div className='flex items-center justify-between mb-1'>
                    <h3 className='font-semibold text-foreground truncate'>
                      {item.title}
                    </h3>
                    <span className='text-xs text-muted-foreground'>
                      {format(new Date(item.createdAt), 'MMM d, yyyy HH:mm')}
                    </span>
                  </div>

                  <p className='text-sm text-muted-foreground line-clamp-2 mb-2'>
                    {item.content}
                  </p>

                  <div className='flex items-center justify-between'>
                    <span className='inline-flex items-center px-2 py-0.5 rounded text-[10px] font-medium bg-muted text-muted-foreground border border-border uppercase'>
                      {item.type.replace('_', ' ')}
                    </span>

                    <button
                      onClick={() => deleteHistory.mutate(item.id)}
                      className='opacity-0 group-hover:opacity-100 p-1.5 hover:bg-destructive/10 hover:text-destructive rounded-md transition-all text-muted-foreground'
                    >
                      <Trash2 className='w-4 h-4' />
                    </button>
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}

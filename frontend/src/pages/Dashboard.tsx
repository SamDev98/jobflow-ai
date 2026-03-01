import { Briefcase, CheckCircle, Clock, TrendingUp } from 'lucide-react'
import { useAnalyticsByStage } from '@/hooks/useAnalytics'
import { useApplications } from '@/hooks/useApplications'
import { STAGE_LABELS, formatDate } from '@/lib/utils'

function StatCard({ label, value, icon: Icon }: { label: string; value: number; icon: React.ElementType }) {
  return (
    <div className="flex items-center gap-4 rounded-xl border bg-card p-5">
      <div className="rounded-lg bg-primary/10 p-3">
        <Icon size={20} className="text-primary" />
      </div>
      <div>
        <p className="text-2xl font-bold">{value}</p>
        <p className="text-sm text-muted-foreground">{label}</p>
      </div>
    </div>
  )
}

export function Dashboard() {
  const { data: analytics } = useAnalyticsByStage()
  const { data: applications } = useApplications()

  const total = Object.values(analytics ?? {}).reduce((a, b) => a + b, 0)
  const active = (analytics?.APPLIED ?? 0) + (analytics?.SCREENING ?? 0) +
    (analytics?.TECHNICAL ?? 0) + (analytics?.ONSITE ?? 0)
  const offers = analytics?.OFFER ?? 0

  const upcoming = (applications?.content ?? [])
    .filter((a) => a.deadline)
    .sort((a, b) => new Date(a.deadline!).getTime() - new Date(b.deadline!).getTime())
    .slice(0, 5)

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Dashboard</h1>

      {/* Stats */}
      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        <StatCard label="Total Applications" value={total} icon={Briefcase} />
        <StatCard label="Active" value={active} icon={Clock} />
        <StatCard label="Offers" value={offers} icon={CheckCircle} />
        <StatCard label="Conversion Rate" value={total > 0 ? Math.round((offers / total) * 100) : 0} icon={TrendingUp} />
      </div>

      {/* By stage */}
      <div className="rounded-xl border bg-card p-5">
        <h2 className="font-semibold mb-4">By Stage</h2>
        <div className="space-y-2">
          {Object.entries(analytics ?? {}).map(([stage, count]) => (
            <div key={stage} className="flex items-center gap-3">
              <span className="w-24 text-xs text-muted-foreground">{STAGE_LABELS[stage]}</span>
              <div className="flex-1 rounded-full bg-muted h-2">
                <div
                  className="rounded-full bg-primary h-2 transition-all"
                  style={{ width: total > 0 ? `${(count / total) * 100}%` : '0%' }}
                />
              </div>
              <span className="text-xs font-medium w-6 text-right">{count}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Upcoming deadlines */}
      {upcoming.length > 0 && (
        <div className="rounded-xl border bg-card p-5">
          <h2 className="font-semibold mb-4">Upcoming Deadlines</h2>
          <div className="space-y-2">
            {upcoming.map((app) => (
              <div key={app.id} className="flex items-center justify-between text-sm">
                <span>{app.role} @ {app.company}</span>
                <span className="text-muted-foreground">{formatDate(app.deadline)}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

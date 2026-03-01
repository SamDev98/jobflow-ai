import type { ExperienceChange } from '@/types'

interface Props {
  changes: ExperienceChange[]
}

export function DiffViewer({ changes }: Props) {
  if (changes.length === 0) return null

  return (
    <div className="space-y-4">
      <h3 className="font-semibold text-sm">Experience Changes</h3>
      {changes.map((change, i) => (
        <div key={i} className="rounded-lg border p-4 space-y-3">
          <p className="text-xs font-medium text-muted-foreground">{change.role}</p>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <p className="text-xs font-semibold mb-1 text-red-600">Before</p>
              <p className="text-xs bg-red-50 rounded p-2 leading-relaxed">{change.originalBullet}</p>
            </div>
            <div>
              <p className="text-xs font-semibold mb-1 text-green-600">After</p>
              <p className="text-xs bg-green-50 rounded p-2 leading-relaxed">{change.optimizedBullet}</p>
            </div>
          </div>

          {change.keywordsAdded.length > 0 && (
            <div className="flex gap-1 flex-wrap">
              {change.keywordsAdded.map((kw) => (
                <span
                  key={kw}
                  className="rounded-full bg-primary/10 text-primary px-2 py-0.5 text-xs font-medium"
                >
                  +{kw}
                </span>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  )
}

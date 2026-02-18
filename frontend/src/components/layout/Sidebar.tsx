import { NavLink } from 'react-router-dom'
import { BarChart3, Kanban, Settings, Wrench } from 'lucide-react'
import { cn } from '@/lib/utils'

const navItems = [
  { to: '/dashboard', label: 'Dashboard', icon: BarChart3 },
  { to: '/pipeline', label: 'Pipeline', icon: Kanban },
  { to: '/tools', label: 'Tools', icon: Wrench },
  { to: '/settings', label: 'Settings', icon: Settings },
]

export function Sidebar() {
  return (
    <aside className="flex h-screen w-56 flex-col border-r bg-card px-3 py-6">
      <div className="mb-8 px-2">
        <span className="text-xl font-bold text-primary">JobFlow AI</span>
      </div>

      <nav className="flex flex-col gap-1">
        {navItems.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              cn(
                'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-primary text-primary-foreground'
                  : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground',
              )
            }
          >
            <Icon size={16} />
            {label}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}

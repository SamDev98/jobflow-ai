import { NavLink, useLocation } from 'react-router-dom';
import {
  BarChart3,
  Kanban,
  Settings,
  Wrench,
  Clock,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { useEffect } from 'react';

const navItems = [
  { to: '/dashboard', label: 'Dashboard', icon: BarChart3 },
  { to: '/pipeline', label: 'Pipeline', icon: Kanban },
  { to: '/tools', label: 'Tools', icon: Wrench },
  { to: '/history', label: 'History', icon: Clock },
  { to: '/settings', label: 'Settings', icon: Settings },
];

interface SidebarProps {
  isCollapsed: boolean;
  setCollapsed: (collapsed: boolean) => void;
  isMobile: boolean;
  isOpen: boolean;
  setIsOpen: (open: boolean) => void;
}

export function Sidebar({
  isCollapsed,
  setCollapsed,
  isMobile,
  isOpen,
  setIsOpen,
}: SidebarProps) {
  const location = useLocation();

  // Close sidebar on navigation on mobile
  useEffect(() => {
    if (isMobile) {
      setIsOpen(false);
    }
  }, [location.pathname, isMobile, setIsOpen]);

  const sidebarClasses = cn(
    'fixed inset-y-0 left-0 z-50 flex flex-col border-r bg-card px-3 py-6 transition-all duration-300 md:relative',
    isCollapsed ? 'w-20' : 'w-56',
    isMobile && !isOpen ? '-translate-x-full' : 'translate-x-0',
  );

  return (
    <>
      {/* Mobile Backdrop */}
      {isMobile && isOpen && (
        <div
          className='fixed inset-0 z-40 bg-black/50 transition-opacity'
          onClick={() => setIsOpen(false)}
        />
      )}

      <aside className={sidebarClasses}>
        <div className='mb-8 flex items-center justify-between px-2'>
          {!isCollapsed && (
            <span className='text-xl font-bold text-primary'>JobFlow AI</span>
          )}
          <button
            onClick={() => setCollapsed(!isCollapsed)}
            className='hidden rounded-md p-1 hover:bg-accent md:block text-muted-foreground'
            title={isCollapsed ? 'Expand Sidebar' : 'Collapse Sidebar'}
          >
            {isCollapsed ? (
              <ChevronRight size={20} />
            ) : (
              <ChevronLeft size={20} />
            )}
          </button>
        </div>

        <nav className='flex flex-col gap-1'>
          {navItems.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                cn(
                  'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-all duration-200',
                  isActive
                    ? 'bg-primary text-primary-foreground shadow-md font-semibold'
                    : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground',
                  isCollapsed ? 'justify-center px-0 h-10 w-10 mx-auto' : '',
                )
              }
              title={isCollapsed ? label : undefined}
            >
              <Icon size={isCollapsed ? 20 : 16} />
              {!isCollapsed && <span>{label}</span>}
            </NavLink>
          ))}
        </nav>
      </aside>
    </>
  );
}

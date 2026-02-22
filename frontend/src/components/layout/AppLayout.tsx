import { Outlet } from 'react-router-dom';
import { UserButton } from '@clerk/clerk-react';
import { Sidebar } from './Sidebar';
import { useState, useEffect } from 'react';
import { Menu, X } from 'lucide-react';

export function AppLayout() {
  const [isCollapsed, setCollapsed] = useState(false);
  const [isOpen, setIsOpen] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth < 768);

  useEffect(() => {
    const handleResize = () => {
      const mobile = window.innerWidth < 768;
      setIsMobile(mobile);
      if (mobile) setCollapsed(false);
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <div className='flex h-screen overflow-hidden bg-background text-foreground'>
      <Sidebar
        isCollapsed={isCollapsed}
        setCollapsed={setCollapsed}
        isMobile={isMobile}
        isOpen={isOpen}
        setIsOpen={setIsOpen}
      />

      <div className='flex flex-1 flex-col overflow-hidden'>
        {/* Top bar */}
        <header className='flex h-14 items-center justify-between border-b bg-card/50 backdrop-blur-sm px-4 md:px-6'>
          <div className='flex items-center gap-4'>
            {isMobile && (
              <button
                onClick={() => setIsOpen(!isOpen)}
                className='p-1 rounded-md hover:bg-accent text-muted-foreground transition-colors'
                aria-label='Toggle Menu'
              >
                {isOpen ? <X size={20} /> : <Menu size={20} />}
              </button>
            )}
            {isMobile && (
              <span className='text-lg font-bold text-primary'>JobFlow AI</span>
            )}
          </div>

          <div className='flex items-center gap-4'>
            <UserButton afterSignOutUrl='/' />
          </div>
        </header>

        {/* Page content */}
        <main className='flex-1 overflow-auto p-4 md:p-6 lg:p-10'>
          <Outlet />
        </main>
      </div>
    </div>
  );
}

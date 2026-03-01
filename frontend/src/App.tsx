import { useEffect } from 'react';
import { BrowserRouter, Route, Routes, Navigate } from 'react-router-dom';
import {
  SignedIn,
  SignedOut,
  RedirectToSignIn,
  useUser,
} from '@clerk/clerk-react';
import { AppLayout } from '@/components/layout/AppLayout';
import { Dashboard } from '@/pages/Dashboard';
import { Pipeline } from '@/pages/Pipeline';
import { History } from '@/pages/History';
import { Tools } from '@/pages/Tools';
import { Settings } from '@/pages/Settings';
import { userApi } from '@/lib/api';
import { isDemoMode } from '@/lib/env';

function SyncUser() {
  const { user } = useUser();

  useEffect(() => {
    if (user?.primaryEmailAddress?.emailAddress) {
      userApi.sync(user.primaryEmailAddress.emailAddress).catch(console.error);
    }
  }, [user]);

  return null;
}

export default function App() {
  if (isDemoMode) {
    return (
      <BrowserRouter>
        <Routes>
          <Route element={<AppLayout />}>
            <Route index element={<Navigate to='/dashboard' replace />} />
            <Route path='/dashboard' element={<Dashboard />} />
            <Route path='/pipeline' element={<Pipeline />} />
            <Route path='/history' element={<History />} />
            <Route path='/tools' element={<Tools />} />
            <Route path='/settings' element={<Settings />} />
          </Route>
        </Routes>
      </BrowserRouter>
    );
  }

  return (
    <BrowserRouter>
      <SignedIn>
        <SyncUser />
        <Routes>
          <Route element={<AppLayout />}>
            <Route index element={<Navigate to='/dashboard' replace />} />
            <Route path='/dashboard' element={<Dashboard />} />
            <Route path='/pipeline' element={<Pipeline />} />
            <Route path='/history' element={<History />} />
            <Route path='/tools' element={<Tools />} />
            <Route path='/settings' element={<Settings />} />
          </Route>
        </Routes>
      </SignedIn>
      <SignedOut>
        <RedirectToSignIn />
      </SignedOut>
    </BrowserRouter>
  );
}

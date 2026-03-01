import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ClerkProvider } from '@clerk/clerk-react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import App from './App';
import { clerkPublishableKey, isDemoMode } from '@/lib/env';
import './index.css';

if (!isDemoMode && !clerkPublishableKey) {
  throw new Error('Missing VITE_CLERK_PUBLISHABLE_KEY in .env');
}

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60, // 1 minute
      retry: 1,
    },
  },
});

const app = (
  <QueryClientProvider client={queryClient}>
    <App />
  </QueryClientProvider>
);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    {isDemoMode ? (
      app
    ) : (
      <ClerkProvider publishableKey={clerkPublishableKey!}>{app}</ClerkProvider>
    )}
  </StrictMode>,
);
/* Deployment Trigger */

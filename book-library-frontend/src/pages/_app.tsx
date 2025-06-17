// pages/_app.tsx
import '@/styles/globals.css';

import type { AppProps } from 'next/app';
import { Inter } from 'next/font/google'; // Import Inter font

const inter = Inter({ subsets: ['latin'] }); // Initialize Inter font

export default function App({ Component, pageProps }: AppProps) {
    return (
        <main className={inter.className}> {/* Apply Inter font to the main content */}
            <Component {...pageProps} />
        </main>
    );
}
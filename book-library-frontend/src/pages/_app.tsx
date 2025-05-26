// pages/_app.tsx
import '@/styles/globals.css';  // путь к твоему globals.css, исправь если надо

import type { AppProps } from 'next/app';

export default function App({ Component, pageProps }: AppProps) {
    return <Component {...pageProps} />;
}

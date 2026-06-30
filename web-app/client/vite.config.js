import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'node:path';
// Vite yapılandırması:
//  - React + Fast Refresh
//  - "@/..." -> "src/..." alias
//  - /api ve /actuator istekleri Spring Boot backend'ine proxy'lenir (CORS yok).
//
// Proxy hedefi VITE_BACKEND_URL'den okunur; yanlışlıkla yol (örn. ".../api") eklense bile
// yalnızca origin (protokol+host+port) kullanılır, böylece "/api/api/..." gibi hatalar olmaz.
export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, process.cwd(), '');
    const raw = env.VITE_BACKEND_URL || 'http://localhost:8085';
    let target = 'http://localhost:8085';
    try {
        target = new URL(raw).origin;
    }
    catch {
        // geçersiz URL -> varsayılan kalsın
    }
    return {
        plugins: [react()],
        resolve: {
            alias: {
                '@': path.resolve(__dirname, 'src'),
            },
        },
        server: {
            port: 5173,
            proxy: {
                '/api': { target, changeOrigin: true },
                '/actuator': { target, changeOrigin: true },
            },
        },
    };
});

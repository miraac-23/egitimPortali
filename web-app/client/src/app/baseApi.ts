import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

// Tüm RTK Query endpoint'lerinin paylaştığı temel API.
// baseUrl olarak göreli "/api" kullanılır -> Vite dev proxy üzerinden
// Spring Boot backend'ine (localhost:8080) iletilir. Böylece tarayıcı isteği
// aynı origin'den (5173) gider; cross-origin / CORS / 403 sorunu yaşanmaz.
const API_BASE = import.meta.env.VITE_API_BASE ?? '/api';

export const baseApi = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({ baseUrl: API_BASE }),
  tagTypes: ['Task', 'Category', 'Topic'],
  endpoints: () => ({}),
});

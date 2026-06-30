import { baseApi } from '@/app/baseApi';

export type RunKind = 'JAVA_FILE' | 'SPRING_BOOT';

export interface RunRequest {
  category: string;
  slug: string;
  file: string; // "Ornek1.java"
  source?: string; // (opsiyonel) ekranda düzenlenmiş kaynak kod
  stdin?: string; // (opsiyonel) programa beslenecek standart girdi (Scanner için)
}

export interface RunResult {
  kind: RunKind;
  target: string; // dosya adı veya FQCN
  command: string;
  exitCode: number | null;
  stopped: boolean;
  durationMs: number;
  truncated: boolean;
  output: string;
  note: string | null;
}

// Örnek kodu backend'de çalıştırır ve çıktısını döndürür (POST /api/run).
export const runnerApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    runExample: builder.mutation<RunResult, RunRequest>({
      query: (body) => ({ url: '/run', method: 'POST', body }),
    }),
  }),
  overrideExisting: false,
});

export const { useRunExampleMutation } = runnerApi;

import { configureStore } from '@reduxjs/toolkit';
import { setupListeners } from '@reduxjs/toolkit/query';
import { baseApi } from './baseApi';
import uiReducer from '@/features/ui/uiSlice';

// RTK Query'nin endpoint'leri injectEndpoints ile baseApi'ye eklendiğinden,
// store'a yalnızca baseApi reducer + middleware'ini bağlamak yeterli.
import '@/features/content/contentApi';
import '@/features/tasks/tasksApi';
import '@/features/runner/runnerApi';

export const store = configureStore({
  reducer: {
    [baseApi.reducerPath]: baseApi.reducer,
    ui: uiReducer,
  },
  middleware: (getDefault) => getDefault().concat(baseApi.middleware),
});

// refetchOnFocus / refetchOnReconnect davranışını etkinleştirir.
setupListeners(store.dispatch);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

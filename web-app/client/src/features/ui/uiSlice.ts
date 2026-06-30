import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

export type ThemeMode = 'light' | 'dark';

interface UiState {
  mode: ThemeMode;
  sidebarOpen: boolean; // mobil drawer durumu
  desktopSidebarOpen: boolean; // masaüstü kalıcı drawer açık/kapalı
}

const STORAGE_KEY = 'egitim-portal-theme';

function initialMode(): ThemeMode {
  if (typeof window !== 'undefined') {
    const saved = window.localStorage.getItem(STORAGE_KEY);
    if (saved === 'light' || saved === 'dark') return saved;
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }
  }
  return 'light';
}

const initialState: UiState = {
  mode: initialMode(),
  sidebarOpen: false,
  desktopSidebarOpen: true,
};

const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    toggleThemeMode(state) {
      state.mode = state.mode === 'light' ? 'dark' : 'light';
      if (typeof window !== 'undefined') {
        window.localStorage.setItem(STORAGE_KEY, state.mode);
      }
    },
    setSidebarOpen(state, action: PayloadAction<boolean>) {
      state.sidebarOpen = action.payload;
    },
    toggleSidebar(state) {
      state.sidebarOpen = !state.sidebarOpen;
    },
    toggleDesktopSidebar(state) {
      state.desktopSidebarOpen = !state.desktopSidebarOpen;
    },
  },
});

export const { toggleThemeMode, setSidebarOpen, toggleSidebar, toggleDesktopSidebar } = uiSlice.actions;
export default uiSlice.reducer;

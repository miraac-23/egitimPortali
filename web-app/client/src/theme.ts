import { createTheme, type Theme } from '@mui/material/styles';
import type { ThemeMode } from '@/features/ui/uiSlice';

// Çok geniş (2K/4K) ekranlar için ek breakpoint. sx içinde `xxl` kullanımına izin verir.
declare module '@mui/material/styles' {
  interface BreakpointOverrides {
    xxl: true;
  }
}

// Modern, yumuşak köşeli ve okunaklı bir tema. Açık/koyu modu destekler.
export function buildTheme(mode: ThemeMode): Theme {
  const isDark = mode === 'dark';
  return createTheme({
    palette: {
      mode,
      primary: { main: '#6750A4' }, // Material You moru
      secondary: { main: '#1B998B' },
      background: isDark
        ? { default: '#121016', paper: '#1C1A22' }
        : { default: '#F6F5FA', paper: '#FFFFFF' },
    },
    breakpoints: {
      values: { xs: 0, sm: 600, md: 900, lg: 1200, xl: 1536, xxl: 2200 },
    },
    shape: { borderRadius: 14 },
    typography: {
      fontFamily: ['Roboto', 'system-ui', '-apple-system', 'Segoe UI', 'sans-serif'].join(','),
      h4: { fontWeight: 700, letterSpacing: -0.5 },
      h5: { fontWeight: 700 },
      h6: { fontWeight: 600 },
      button: { textTransform: 'none', fontWeight: 600 },
    },
    components: {
      MuiCard: {
        styleOverrides: {
          root: {
            backgroundImage: 'none',
            border: `1px solid ${isDark ? 'rgba(255,255,255,0.08)' : 'rgba(0,0,0,0.06)'}`,
          },
        },
      },
      MuiAppBar: {
        styleOverrides: {
          root: {
            backgroundImage: 'none',
            backdropFilter: 'blur(8px)',
          },
        },
      },
      MuiButton: { defaultProps: { disableElevation: true } },
    },
  });
}

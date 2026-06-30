import { useMemo } from 'react';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import { buildTheme } from './theme';
import { useAppSelector } from './app/hooks';

// Redux'taki tema modunu MUI ThemeProvider'a bağlar.
export default function ThemedApp() {
  const mode = useAppSelector((s) => s.ui.mode);
  const theme = useMemo(() => buildTheme(mode), [mode]);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </ThemeProvider>
  );
}

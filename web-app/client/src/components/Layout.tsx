import { useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import {
  AppBar,
  Box,
  Drawer,
  IconButton,
  InputBase,
  Toolbar,
  Tooltip,
  Typography,
  alpha,
  useMediaQuery,
  useTheme,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import MenuOpenIcon from '@mui/icons-material/MenuOpen';
import SearchIcon from '@mui/icons-material/Search';
import LightModeRoundedIcon from '@mui/icons-material/LightModeRounded';
import DarkModeRoundedIcon from '@mui/icons-material/DarkModeRounded';
import GitHubIcon from '@mui/icons-material/GitHub';
import Sidebar from './Sidebar';
import ApiStatusIndicator from './ApiStatusIndicator';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { setSidebarOpen, toggleDesktopSidebar, toggleSidebar, toggleThemeMode } from '@/features/ui/uiSlice';

const DRAWER_WIDTH = 290;

export default function Layout() {
  const theme = useTheme();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const mode = useAppSelector((s) => s.ui.mode);
  const sidebarOpen = useAppSelector((s) => s.ui.sidebarOpen);
  const desktopSidebarOpen = useAppSelector((s) => s.ui.desktopSidebarOpen);
  const isDesktop = useMediaQuery(theme.breakpoints.up('md'));
  const [search, setSearch] = useState('');

  const closeMobile = () => dispatch(setSidebarOpen(false));
  // Tek buton: masaüstünde kalıcı drawer'ı, mobilde geçici drawer'ı açıp kapatır.
  const toggleNav = () => dispatch(isDesktop ? toggleDesktopSidebar() : toggleSidebar());
  // Masaüstünde drawer ne kadar yer kaplıyor?
  const navWidth = isDesktop && desktopSidebarOpen ? DRAWER_WIDTH : 0;

  const submitSearch = (e: React.FormEvent) => {
    e.preventDefault();
    const q = search.trim();
    if (q) navigate(`/search?q=${encodeURIComponent(q)}`);
  };

  const drawerContent = <Sidebar onNavigate={isDesktop ? undefined : closeMobile} />;

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          zIndex: (t) => t.zIndex.drawer + 1,
          bgcolor: alpha(theme.palette.background.paper, 0.8),
          color: 'text.primary',
          borderBottom: 1,
          borderColor: 'divider',
        }}
      >
        <Toolbar sx={{ gap: 1 }}>
          <Tooltip title={(isDesktop ? desktopSidebarOpen : sidebarOpen) ? 'Menüyü gizle' : 'Menüyü göster'}>
            <IconButton edge="start" onClick={toggleNav} aria-label="menüyü aç/kapat">
              {(isDesktop ? desktopSidebarOpen : sidebarOpen) ? <MenuOpenIcon /> : <MenuIcon />}
            </IconButton>
          </Tooltip>
          <Typography
            variant="h6"
            sx={{ fontWeight: 800, cursor: 'pointer', mr: 2, whiteSpace: 'nowrap' }}
            onClick={() => navigate('/')}
          >
            ☕ Eğitim Portalı
          </Typography>

          <Box
            component="form"
            onSubmit={submitSearch}
            sx={{
              position: 'relative',
              borderRadius: 2,
              bgcolor: alpha(theme.palette.text.primary, 0.06),
              '&:hover': { bgcolor: alpha(theme.palette.text.primary, 0.1) },
              flexGrow: 1,
              maxWidth: 420,
              display: 'flex',
              alignItems: 'center',
            }}
          >
            <Box sx={{ px: 1.5, display: 'flex', color: 'text.secondary' }}>
              <SearchIcon fontSize="small" />
            </Box>
            <InputBase
              placeholder="Konularda ara…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              sx={{ flexGrow: 1, py: 0.75, pr: 1 }}
              inputProps={{ 'aria-label': 'ara' }}
            />
          </Box>

          <Box sx={{ flexGrow: 1 }} />

          <Box sx={{ display: { xs: 'none', sm: 'block' } }}>
            <ApiStatusIndicator />
          </Box>
          <Tooltip title={mode === 'dark' ? 'Açık tema' : 'Koyu tema'}>
            <IconButton onClick={() => dispatch(toggleThemeMode())} color="inherit">
              {mode === 'dark' ? <LightModeRoundedIcon /> : <DarkModeRoundedIcon />}
            </IconButton>
          </Tooltip>
          <Tooltip title="Kaynak repo">
            <IconButton
              color="inherit"
              component="a"
              href="https://github.com"
              target="_blank"
              rel="noopener noreferrer"
            >
              <GitHubIcon />
            </IconButton>
          </Tooltip>
        </Toolbar>
      </AppBar>

      {/* Masaüstü: kalıcı drawer (açıkken) / Mobil: geçici drawer */}
      <Box component="nav" sx={{ width: { md: navWidth }, flexShrink: { md: 0 }, transition: 'width 0.2s' }}>
        {isDesktop ? (
          desktopSidebarOpen && (
            <Drawer
              variant="permanent"
              open
              sx={{
                '& .MuiDrawer-paper': {
                  width: DRAWER_WIDTH,
                  boxSizing: 'border-box',
                  borderRight: 1,
                  borderColor: 'divider',
                  bgcolor: 'background.default',
                },
              }}
            >
              <Toolbar />
              {drawerContent}
            </Drawer>
          )
        ) : (
          <Drawer
            variant="temporary"
            open={sidebarOpen}
            onClose={closeMobile}
            ModalProps={{ keepMounted: true }}
            sx={{ '& .MuiDrawer-paper': { width: DRAWER_WIDTH, boxSizing: 'border-box' } }}
          >
            <Toolbar />
            {drawerContent}
          </Drawer>
        )}
      </Box>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          width: { md: `calc(100% - ${navWidth}px)` },
          minHeight: '100vh',
          bgcolor: 'background.default',
          transition: 'width 0.2s',
        }}
      >
        <Toolbar />
        <Box sx={{ p: { xs: 2, md: 4 }, maxWidth: { xs: '100%', lg: 1500, xl: 1840, xxl: 2200 }, mx: 'auto' }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
}

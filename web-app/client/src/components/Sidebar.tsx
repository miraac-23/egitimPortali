import { useMemo, useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import {
  Box,
  Chip,
  Collapse,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Skeleton,
  Typography,
} from '@mui/material';
import { alpha } from '@mui/material/styles';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import ScienceRoundedIcon from '@mui/icons-material/ScienceRounded';
import CoffeeRoundedIcon from '@mui/icons-material/CoffeeRounded';
import SpaRoundedIcon from '@mui/icons-material/SpaRounded';
import HistoryEduRoundedIcon from '@mui/icons-material/HistoryEduRounded';
import ExpandLess from '@mui/icons-material/ExpandLess';
import ExpandMore from '@mui/icons-material/ExpandMore';
import FiberManualRecordIcon from '@mui/icons-material/FiberManualRecord';
import { useGetCategoriesQuery, useGetTopicsQuery } from '@/features/content/contentApi';

interface SidebarProps {
  onNavigate?: () => void; // mobil drawer'ı kapatmak için
}

export default function Sidebar({ onNavigate }: SidebarProps) {
  const { data: categories, isLoading: catLoading } = useGetCategoriesQuery();
  const { data: topics, isLoading: topLoading } = useGetTopicsQuery();
  const location = useLocation();

  const topicsByCategory = useMemo(() => {
    const map: Record<string, typeof topics> = {};
    (topics ?? []).forEach((t) => {
      (map[t.category] ??= []).push(t);
    });
    return map;
  }, [topics]);

  const activeCategory = useMemo(() => {
    const m = location.pathname.match(/^\/topic\/([^/]+)/);
    return m ? m[1] : null;
  }, [location.pathname]);

  const [open, setOpen] = useState<Record<string, boolean>>({});
  const isOpen = (id: string) => open[id] ?? id === activeCategory;
  const toggle = (id: string) => setOpen((p) => ({ ...p, [id]: !isOpen(id) }));

  const catIcon = (id: string) =>
    id === '01-java' ? (
      <CoffeeRoundedIcon fontSize="small" />
    ) : id === '05-java-versiyon-analizi' ? (
      <HistoryEduRoundedIcon fontSize="small" />
    ) : (
      <SpaRoundedIcon fontSize="small" />
    );

  const loading = catLoading || topLoading;

  return (
    <Box
      sx={{
        height: '100%',
        flex: 1,
        minHeight: 0,
        display: 'flex',
        flexDirection: 'column',
        // İnce, modern kaydırma çubuğu
        '& *::-webkit-scrollbar': { width: 8 },
        '& *::-webkit-scrollbar-thumb': {
          borderRadius: 8,
          bgcolor: (t) => alpha(t.palette.text.primary, 0.18),
        },
      }}
    >
      {/* Üst sabit kısayollar */}
      <Box sx={{ px: 1.5, pt: 1.5, pb: 1 }}>
        {[
          { to: '/', icon: <HomeRoundedIcon fontSize="small" />, label: 'Ana Sayfa', end: true },
          { to: '/demo', icon: <ScienceRoundedIcon fontSize="small" />, label: 'Canlı Demo (Task API)', end: false },
        ].map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            style={{ textDecoration: 'none', color: 'inherit' }}
            onClick={onNavigate}
          >
            {({ isActive }) => (
              <ListItemButton selected={isActive} sx={navItemSx}>
                <ListItemIcon sx={{ minWidth: 34, color: isActive ? 'primary.main' : 'text.secondary' }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText primary={item.label} primaryTypographyProps={{ variant: 'body2', fontWeight: 600 }} />
              </ListItemButton>
            )}
          </NavLink>
        ))}
      </Box>

      {/* Kaydırılabilir kategori/konu listesi */}
      <Box sx={{ flexGrow: 1, overflowY: 'auto', px: 1.5, pb: 2 }}>
        {loading && (
          <Box sx={{ pt: 1 }}>
            {Array.from({ length: 9 }).map((_, i) => (
              <Skeleton key={i} height={36} sx={{ my: 0.5, borderRadius: 1 }} />
            ))}
          </Box>
        )}

        {(categories ?? []).map((cat) => {
          const opened = isOpen(cat.id);
          const catActive = activeCategory === cat.id;
          return (
            <Box key={cat.id} sx={{ mb: 0.5 }}>
              {/* Bölüm başlığı: AKIŞIN PARÇASI (sticky değil) → kaydırınca kaymaz/üst üste binmez */}
              <ListItemButton onClick={() => toggle(cat.id)} sx={categoryHeaderSx(catActive)}>
                <ListItemIcon sx={{ minWidth: 32, color: catActive ? 'primary.main' : 'text.secondary' }}>
                  {catIcon(cat.id)}
                </ListItemIcon>
                <ListItemText
                  primary={cat.title}
                  primaryTypographyProps={{
                    variant: 'subtitle2',
                    fontWeight: 700,
                    noWrap: true,
                    letterSpacing: 0.2,
                  }}
                />
                <Chip
                  size="small"
                  label={cat.topicCount}
                  sx={{
                    height: 20,
                    fontSize: '0.68rem',
                    fontWeight: 700,
                    mr: 0.5,
                    bgcolor: (t) => alpha(t.palette.primary.main, catActive ? 0.18 : 0.1),
                    color: catActive ? 'primary.main' : 'text.secondary',
                  }}
                />
                {opened ? <ExpandLess fontSize="small" /> : <ExpandMore fontSize="small" />}
              </ListItemButton>

              <Collapse in={opened} timeout="auto" unmountOnExit>
                <List dense disablePadding sx={{ mt: 0.25 }}>
                  {(topicsByCategory[cat.id] ?? []).map((t) => (
                    <NavLink
                      key={t.id}
                      to={`/topic/${t.category}/${t.slug}`}
                      style={{ textDecoration: 'none', color: 'inherit' }}
                      onClick={onNavigate}
                    >
                      {({ isActive }) => (
                        <ListItemButton selected={isActive} sx={topicItemSx}>
                          <ListItemIcon sx={{ minWidth: 26 }}>
                            <FiberManualRecordIcon
                              sx={{ fontSize: '0.5rem', color: isActive ? 'primary.main' : 'text.disabled' }}
                            />
                          </ListItemIcon>
                          <ListItemText
                            primary={t.title}
                            primaryTypographyProps={{
                              variant: 'body2',
                              noWrap: true,
                              fontWeight: isActive ? 700 : 400,
                            }}
                          />
                        </ListItemButton>
                      )}
                    </NavLink>
                  ))}
                </List>
              </Collapse>
            </Box>
          );
        })}

        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', px: 1, mt: 3, opacity: 0.7 }}>
          Java &amp; Spring Eğitim Portalı
        </Typography>
      </Box>
    </Box>
  );
}

// --- Ortak stiller ---------------------------------------------------------

const navItemSx = {
  borderRadius: 2,
  mb: 0.5,
  py: 0.75,
  '&.Mui-selected': {
    bgcolor: (t: any) => alpha(t.palette.primary.main, 0.12),
    '&:hover': { bgcolor: (t: any) => alpha(t.palette.primary.main, 0.18) },
  },
};

const categoryHeaderSx = (active: boolean) => ({
  borderRadius: 2,
  py: 0.85,
  bgcolor: (t: any) => (active ? alpha(t.palette.primary.main, 0.08) : 'transparent'),
  '&:hover': { bgcolor: (t: any) => alpha(t.palette.text.primary, 0.05) },
});

const topicItemSx = {
  borderRadius: 2,
  ml: 1.5,
  pl: 1.5,
  py: 0.5,
  position: 'relative' as const,
  '&.Mui-selected': {
    bgcolor: (t: any) => alpha(t.palette.primary.main, 0.12),
    '&:hover': { bgcolor: (t: any) => alpha(t.palette.primary.main, 0.18) },
    '&::before': {
      content: '""',
      position: 'absolute',
      left: 0,
      top: 6,
      bottom: 6,
      width: 3,
      borderRadius: 3,
      bgcolor: 'primary.main',
    },
  },
};

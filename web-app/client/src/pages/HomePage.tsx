import { useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  Card,
  CardActionArea,
  CardContent,
  Chip,
  Paper,
  Skeleton,
  Stack,
  Typography,
} from '@mui/material';
import MenuBookRoundedIcon from '@mui/icons-material/MenuBookRounded';
import CodeRoundedIcon from '@mui/icons-material/CodeRounded';
import ScienceRoundedIcon from '@mui/icons-material/ScienceRounded';
import LayersRoundedIcon from '@mui/icons-material/LayersRounded';
import { useGetCategoriesQuery, useGetTopicsQuery } from '@/features/content/contentApi';
import type { TopicSummary } from '@/features/content/types';

function StatCard({ icon, value, label }: { icon: React.ReactNode; value: React.ReactNode; label: string }) {
  return (
    <Paper sx={{ p: 2.5, display: 'flex', alignItems: 'center', gap: 2, borderRadius: 3 }} elevation={0} variant="outlined">
      <Box
        sx={{
          width: 48,
          height: 48,
          borderRadius: 2,
          display: 'grid',
          placeItems: 'center',
          bgcolor: 'primary.main',
          color: 'primary.contrastText',
        }}
      >
        {icon}
      </Box>
      <Box>
        <Typography variant="h5">{value}</Typography>
        <Typography variant="body2" color="text.secondary">
          {label}
        </Typography>
      </Box>
    </Paper>
  );
}

function TopicCard({ topic }: { topic: TopicSummary }) {
  const navigate = useNavigate();
  return (
    <Card variant="outlined" sx={{ borderRadius: 3, height: '100%' }}>
      <CardActionArea
        sx={{ height: '100%', alignItems: 'stretch' }}
        onClick={() => navigate(`/topic/${topic.category}/${topic.slug}`)}
      >
        <CardContent>
          <Stack direction="row" spacing={1} sx={{ mb: 1 }}>
            <Chip size="small" label={topic.categoryTitle} color="primary" variant="outlined" />
            {topic.levels.map((l) => (
              <Chip key={l.key} size="small" label={l.label} sx={{ fontSize: '0.7rem' }} />
            ))}
          </Stack>
          <Typography variant="h6" sx={{ mb: 0.5 }}>
            {topic.title}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ display: '-webkit-box', WebkitLineClamp: 3, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
            {topic.summary || 'Bu konunun README dosyasını ve örnek kodlarını inceleyin.'}
          </Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
}

export default function HomePage() {
  const navigate = useNavigate();
  const { data: categories, isLoading: catLoading } = useGetCategoriesQuery();
  const { data: topics, isLoading: topLoading } = useGetTopicsQuery();

  const totalTopics = topics?.length ?? 0;
  const totalCode = useMemo(
    () => (topics ?? []).reduce((sum, t) => sum + t.levels.length, 0),
    [topics],
  );
  const featured = useMemo(() => (topics ?? []).slice(0, 6), [topics]);

  return (
    <Stack spacing={4}>
      {/* Hero */}
      <Paper
        elevation={0}
        sx={{
          p: { xs: 3, md: 5 },
          borderRadius: 4,
          color: 'common.white',
          background: 'linear-gradient(135deg, #6750A4 0%, #1B998B 100%)',
        }}
      >
        <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>
          Java &amp; Spring Eğitim Portalı
        </Typography>
        <Typography variant="body1" sx={{ opacity: 0.95, maxWidth: 720, mb: 3 }}>
          Çalıştırılabilir bir araştırma çalışmasının interaktif yüzü. Her konuyu
          <strong> Temel → Orta → İleri</strong> akışıyla okuyun, örnek kodları
          syntax-highlight ile inceleyin ve canlı bir Spring Boot REST API'sini
          tarayıcıdan deneyin.
        </Typography>
        <Stack direction="row" spacing={2} flexWrap="wrap" useFlexGap>
          <Button variant="contained" color="inherit" sx={{ color: 'primary.main' }} startIcon={<MenuBookRoundedIcon />} onClick={() => topics && navigate(`/topic/${topics[0].category}/${topics[0].slug}`)}>
            Okumaya Başla
          </Button>
          <Button variant="outlined" color="inherit" startIcon={<ScienceRoundedIcon />} onClick={() => navigate('/demo')}>
            Canlı Demo'yu Aç
          </Button>
        </Stack>
      </Paper>

      {/* İstatistikler */}
      <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr 1fr', md: 'repeat(4, 1fr)' } }}>
        {catLoading || topLoading ? (
          Array.from({ length: 4 }).map((_, i) => <Skeleton key={i} variant="rounded" height={88} />)
        ) : (
          <>
            <StatCard icon={<LayersRoundedIcon />} value={categories?.length ?? 0} label="Ana Kategori" />
            <StatCard icon={<MenuBookRoundedIcon />} value={totalTopics} label="Konu" />
            <StatCard icon={<CodeRoundedIcon />} value={totalCode} label="Örnek Kod Dosyası" />
            <StatCard icon={<ScienceRoundedIcon />} value="1" label="Canlı REST API" />
          </>
        )}
      </Box>

      {/* Kategoriler */}
      <Box>
        <Typography variant="h5" sx={{ mb: 2 }}>
          Kategoriler
        </Typography>
        <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' } }}>
          {(categories ?? []).map((cat) => (
            <Card key={cat.id} variant="outlined" sx={{ borderRadius: 3 }}>
              <CardActionArea onClick={() => navigate(`/search?category=${cat.id}`)}>
                <CardContent>
                  <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
                    <Typography variant="h6">{cat.title}</Typography>
                    <Chip size="small" label={`${cat.topicCount} konu`} color="primary" />
                  </Stack>
                  <Typography variant="body2" color="text.secondary">
                    {cat.description}
                  </Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          ))}
        </Box>
      </Box>

      {/* Öne çıkan konular */}
      <Box>
        <Typography variant="h5" sx={{ mb: 2 }}>
          Öne Çıkan Konular
        </Typography>
        <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', lg: 'repeat(3, 1fr)' } }}>
          {topLoading
            ? Array.from({ length: 6 }).map((_, i) => <Skeleton key={i} variant="rounded" height={160} />)
            : featured.map((t) => <TopicCard key={t.id} topic={t} />)}
        </Box>
      </Box>
    </Stack>
  );
}

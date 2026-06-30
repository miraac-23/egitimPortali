import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  Box,
  Card,
  CardActionArea,
  CardContent,
  Chip,
  Skeleton,
  Stack,
  Typography,
} from '@mui/material';
import SearchOffRoundedIcon from '@mui/icons-material/SearchOffRounded';
import { useGetTopicsQuery } from '@/features/content/contentApi';

export default function SearchPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const q = params.get('q') ?? undefined;
  const category = params.get('category') ?? undefined;

  const { data: topics, isLoading } = useGetTopicsQuery({ q, category });

  const heading = q
    ? `"${q}" için sonuçlar`
    : category
      ? `Kategori: ${category === '01-java' ? 'Java' : 'Spring & Spring Boot'}`
      : 'Tüm Konular';

  return (
    <Stack spacing={3}>
      <Typography variant="h4">{heading}</Typography>

      {isLoading ? (
        <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' } }}>
          {Array.from({ length: 6 }).map((_, i) => (
            <Skeleton key={i} variant="rounded" height={130} />
          ))}
        </Box>
      ) : (topics ?? []).length === 0 ? (
        <Stack alignItems="center" spacing={1} sx={{ py: 8, color: 'text.secondary' }}>
          <SearchOffRoundedIcon sx={{ fontSize: 48 }} />
          <Typography>Eşleşen konu bulunamadı.</Typography>
        </Stack>
      ) : (
        <>
          <Typography variant="body2" color="text.secondary">
            {topics!.length} konu bulundu
          </Typography>
          <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' } }}>
            {topics!.map((t) => (
              <Card key={t.id} variant="outlined" sx={{ borderRadius: 3 }}>
                <CardActionArea onClick={() => navigate(`/topic/${t.category}/${t.slug}`)}>
                  <CardContent>
                    <Stack direction="row" spacing={1} sx={{ mb: 1 }}>
                      <Chip size="small" color="primary" variant="outlined" label={t.categoryTitle} />
                    </Stack>
                    <Typography variant="h6" sx={{ mb: 0.5 }}>
                      {t.title}
                    </Typography>
                    <Typography
                      variant="body2"
                      color="text.secondary"
                      sx={{ display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}
                    >
                      {t.summary}
                    </Typography>
                  </CardContent>
                </CardActionArea>
              </Card>
            ))}
          </Box>
        </>
      )}
    </Stack>
  );
}

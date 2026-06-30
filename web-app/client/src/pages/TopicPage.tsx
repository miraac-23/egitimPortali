import { useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import {
  Alert,
  Box,
  Chip,
  Paper,
  Skeleton,
  Stack,
  Tab,
  Tabs,
  Typography,
} from '@mui/material';
import DescriptionRoundedIcon from '@mui/icons-material/DescriptionRounded';
import CodeRoundedIcon from '@mui/icons-material/CodeRounded';
import { useGetTopicQuery } from '@/features/content/contentApi';
import Markdown from '@/components/Markdown';
import ExampleRunner from '@/features/runner/ExampleRunner';

export default function TopicPage() {
  const { category = '', slug = '' } = useParams();
  const { data, isLoading, isError, error } = useGetTopicQuery({ category, slug });
  const [tab, setTab] = useState(0); // 0 = README, 1..n = kod dosyaları

  // Konu değişince sekmeyi README'ye al ve sayfa başına dön.
  useEffect(() => {
    setTab(0);
    window.scrollTo({ top: 0 });
  }, [category, slug]);

  // README içindeki ".java" bağlantısı tıklanınca ilgili kod sekmesine geç.
  const openCodeFile = useCallback(
    (fileName: string) => {
      const files = data?.codeFiles ?? [];
      const idx = files.findIndex((c) => c.fileName === fileName);
      if (idx >= 0) {
        setTab(idx + 1);
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    },
    [data],
  );

  if (isLoading) {
    return (
      <Stack spacing={2}>
        <Skeleton variant="text" width="50%" height={48} />
        <Skeleton variant="rounded" height={40} />
        <Skeleton variant="rounded" height={400} />
      </Stack>
    );
  }

  if (isError || !data) {
    const status = (error as { status?: number } | undefined)?.status;
    return (
      <Alert severity="error" sx={{ mt: 2 }}>
        Konu yüklenemedi{status ? ` (HTTP ${status})` : ''}. Backend çalışıyor mu? Adres: <code>{category}/{slug}</code>
      </Alert>
    );
  }

  const codeFiles = data.codeFiles;

  return (
    <Stack spacing={2}>
      <Box>
        <Stack direction="row" spacing={1} sx={{ mb: 1 }} flexWrap="wrap" useFlexGap>
          <Chip size="small" color="primary" label={data.categoryTitle} />
          <Chip size="small" variant="outlined" label={data.slug} />
          {codeFiles.map((c) => (
            <Chip key={c.key} size="small" variant="outlined" label={`${c.label} · ${c.lineCount} satır`} />
          ))}
        </Stack>
        <Typography variant="h4">{data.title}</Typography>
      </Box>

      <Paper variant="outlined" sx={{ borderRadius: 3, overflow: 'hidden' }}>
        <Tabs
          value={tab}
          onChange={(_e, v) => setTab(v)}
          variant="scrollable"
          scrollButtons="auto"
          sx={{ borderBottom: 1, borderColor: 'divider', px: 1 }}
        >
          <Tab icon={<DescriptionRoundedIcon fontSize="small" />} iconPosition="start" label="Anlatım (README)" />
          {codeFiles.map((c) => (
            <Tab
              key={c.key}
              icon={<CodeRoundedIcon fontSize="small" />}
              iconPosition="start"
              label={c.label}
            />
          ))}
        </Tabs>

        <Box sx={{ p: { xs: 2, md: 3 } }}>
          {tab === 0 ? (
            data.readme ? (
              <Markdown onOpenCodeFile={openCodeFile}>{data.readme}</Markdown>
            ) : (
              <Typography color="text.secondary">Bu konu için README bulunamadı.</Typography>
            )
          ) : (
            (() => {
              const file = codeFiles[tab - 1];
              if (!file) return null;
              return (
                <ExampleRunner key={file.fileName} category={data.category} slug={data.slug} file={file} />
              );
            })()
          )}
        </Box>
      </Paper>
    </Stack>
  );
}

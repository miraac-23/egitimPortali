import { useNavigate } from 'react-router-dom';
import { Button, Stack, Typography } from '@mui/material';

export default function NotFoundPage() {
  const navigate = useNavigate();
  return (
    <Stack alignItems="center" spacing={2} sx={{ py: 10 }}>
      <Typography variant="h2" sx={{ fontWeight: 800 }}>
        404
      </Typography>
      <Typography color="text.secondary">Aradığınız sayfa bulunamadı.</Typography>
      <Button variant="contained" onClick={() => navigate('/')}>
        Ana Sayfaya Dön
      </Button>
    </Stack>
  );
}

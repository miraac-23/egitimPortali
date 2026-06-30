import { useEffect, useState } from 'react';
import { Chip, Tooltip } from '@mui/material';
import CircleIcon from '@mui/icons-material/Circle';

type Status = 'up' | 'down' | 'checking';

// Backend /actuator/health'i periyodik yoklayıp API bağlantı durumunu gösterir.
export default function ApiStatusIndicator() {
  const [status, setStatus] = useState<Status>('checking');

  useEffect(() => {
    let active = true;
    const check = async () => {
      try {
        const res = await fetch('/actuator/health');
        const body = (await res.json()) as { status?: string };
        if (active) setStatus(res.ok && body.status === 'UP' ? 'up' : 'down');
      } catch {
        if (active) setStatus('down');
      }
    };
    check();
    const id = setInterval(check, 10000);
    return () => {
      active = false;
      clearInterval(id);
    };
  }, []);

  const config: Record<Status, { label: string; color: 'success' | 'error' | 'default'; tip: string }> = {
    up: { label: 'API bağlı', color: 'success', tip: 'Spring Boot backend çalışıyor (localhost:8080)' },
    down: { label: 'API kapalı', color: 'error', tip: 'Backend yanıt vermiyor. ./gradlew bootRun ile başlatın.' },
    checking: { label: 'Kontrol...', color: 'default', tip: 'API durumu kontrol ediliyor' },
  };
  const c = config[status];

  return (
    <Tooltip title={c.tip}>
      <Chip
        size="small"
        color={c.color}
        variant="outlined"
        icon={<CircleIcon sx={{ fontSize: '0.7rem !important' }} />}
        label={c.label}
        sx={{ fontWeight: 600 }}
      />
    </Tooltip>
  );
}

import { useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Collapse,
  Divider,
  IconButton,
  Stack,
  Tooltip,
  Typography,
} from '@mui/material';
import PlayArrowRoundedIcon from '@mui/icons-material/PlayArrowRounded';
import TerminalRoundedIcon from '@mui/icons-material/TerminalRounded';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';
import CodeRoundedIcon from '@mui/icons-material/CodeRounded';
import EditRoundedIcon from '@mui/icons-material/EditRounded';
import VisibilityRoundedIcon from '@mui/icons-material/VisibilityRounded';
import RestartAltRoundedIcon from '@mui/icons-material/RestartAltRounded';
import KeyboardRoundedIcon from '@mui/icons-material/KeyboardRounded';
import CodeBlock from '@/components/CodeBlock';
import CodeEditor from '@/components/CodeEditor';
import type { CodeFile } from '@/features/content/types';
import { useRunExampleMutation, type RunResult } from './runnerApi';

interface Props {
  category: string;
  slug: string;
  file: CodeFile;
}

// Bir örneği gösterir + DÜZENLEYİP çalıştırır. "Çalıştır" düğmesi kodun HEM üstünde HEM altında.
// Ek olarak: kodu ekranda düzenleme, programa stdin (klavye) girdisi verme ve sıfırlama.
// (TopicPage bunu file.fileName ile key'ler; dosya değişince durum sıfırlanır.)
export default function ExampleRunner({ category, slug, file }: Props) {
  const [runExample, { data, error, isLoading, reset }] = useRunExampleMutation();
  const [open, setOpen] = useState(false);

  const [source, setSource] = useState(file.content);
  const [stdin, setStdin] = useState('');
  const [editing, setEditing] = useState(false);
  const [showInput, setShowInput] = useState(false);

  const edited = source !== file.content;
  const satirSayisi = source.split('\n').length;

  const handleRun = async () => {
    setOpen(true);
    try {
      await runExample({
        category,
        slug,
        file: file.fileName,
        source: edited ? source : undefined, // düzenlendiyse düzenlenmiş kaynağı gönder
        stdin: stdin.length > 0 ? stdin : undefined,
      }).unwrap();
    } catch {
      /* hata aşağıda gösterilir */
    }
  };

  const handleReset = () => {
    setSource(file.content);
    setStdin('');
    setEditing(false);
  };

  const result = data as RunResult | undefined;

  const calistirButonu = (
    <Button
      variant="contained"
      color="success"
      size="small"
      startIcon={isLoading ? <CircularProgress size={16} color="inherit" /> : <PlayArrowRoundedIcon />}
      onClick={handleRun}
      disabled={isLoading}
    >
      {isLoading ? 'Çalıştırılıyor…' : 'Çalıştır'}
    </Button>
  );

  return (
    <Box>
      {/* Üst araç çubuğu: dosya adı + düzenle/görüntüle + sıfırla + (ÜSTTEKİ) Çalıştır */}
      <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1.5 }} flexWrap="wrap" useFlexGap>
        <CodeRoundedIcon fontSize="small" color="primary" />
        <Typography variant="subtitle1" sx={{ fontFamily: '"JetBrains Mono", monospace' }}>
          {file.fileName}
        </Typography>
        <Chip size="small" label={`${satirSayisi} satır`} />
        {edited && <Chip size="small" color="warning" label="düzenlendi" />}
        <Box sx={{ flexGrow: 1 }} />
        <Tooltip title={editing ? 'Salt görünüme dön (vurgulu)' : 'Kodu düzenle'}>
          <Button
            size="small"
            variant="outlined"
            startIcon={editing ? <VisibilityRoundedIcon /> : <EditRoundedIcon />}
            onClick={() => setEditing((v) => !v)}
          >
            {editing ? 'Görüntüle' : 'Düzenle'}
          </Button>
        </Tooltip>
        <Tooltip title="Kodu ve girdiyi orijinaline döndür">
          <span>
            <Button
              size="small"
              variant="outlined"
              color="inherit"
              startIcon={<RestartAltRoundedIcon />}
              onClick={handleReset}
              disabled={!edited && stdin.length === 0}
            >
              Sıfırla
            </Button>
          </span>
        </Tooltip>
        {calistirButonu}
      </Stack>

      <Divider sx={{ mb: 2 }} />

      {/* Kod alanı: düzenleme modunda IDE tarzı editör, değilse vurgulu görünüm */}
      {editing ? (
        <CodeEditor value={source} onChange={setSource} language={file.language} />
      ) : (
        <CodeBlock code={source} language={file.language} />
      )}

      {/* Girdi (stdin) bölümü — Scanner ile klavyeden okuyan programlar için */}
      <Box sx={{ mt: 1.5 }}>
        <Button
          size="small"
          color="inherit"
          startIcon={<KeyboardRoundedIcon />}
          onClick={() => setShowInput((v) => !v)}
        >
          {showInput ? 'Girdiyi gizle' : 'Girdi (stdin) ekle'}
          {stdin.length > 0 && !showInput ? ' • dolu' : ''}
        </Button>
        <Collapse in={showInput} timeout="auto" unmountOnExit>
          <Box
            component="textarea"
            value={stdin}
            spellCheck={false}
            placeholder={'Programın klavyeden okuduğu girdiyi buraya yaz.\nHer satır bir giriş satırıdır (Scanner.nextLine / nextInt).'}
            onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => setStdin(e.target.value)}
            sx={{
              mt: 1,
              width: '100%',
              minHeight: 90,
              boxSizing: 'border-box',
              p: 1.5,
              borderRadius: 2,
              border: 1,
              borderColor: 'divider',
              bgcolor: 'background.paper',
              color: 'text.primary',
              fontFamily: '"JetBrains Mono", monospace',
              fontSize: '0.8rem',
              lineHeight: 1.6,
              resize: 'vertical',
              outline: 'none',
            }}
          />
          <Typography variant="caption" color="text.secondary" component="div">
            Bu girdi programın standart girişine (stdin) beslenir; böylece <code>Scanner</code> ile
            yazılmış programları da çalıştırabilirsin.
          </Typography>
        </Collapse>
      </Box>

      {/* Alt: (ALTTAKİ) Çalıştır düğmesi + çıktı konsolu */}
      <Box sx={{ mt: 2 }}>
        <Stack direction="row" spacing={1.5} alignItems="center" flexWrap="wrap" useFlexGap>
          {calistirButonu}
          <Typography variant="caption" color="text.secondary">
            {edited ? 'Düzenlenmiş kod' : file.fileName} backend'de çalıştırılır, çıktı aşağıda görünür.
          </Typography>
        </Stack>

        {isLoading && (
          <Alert severity="info" icon={<TerminalRoundedIcon />} sx={{ mt: 1.5 }}>
            Kod backend'de derlenip çalıştırılıyor… Spring/Spring Boot örnekleri ilk çalıştırmada
            <strong> bir dakikaya kadar</strong> sürebilir.
          </Alert>
        )}

        {!!error && !isLoading && (
          <Alert severity="error" sx={{ mt: 1.5 }}>
            Çalıştırma isteği başarısız oldu. Backend (<code>:8085</code>) çalışıyor mu?
          </Alert>
        )}

        <Collapse in={open && !!result} timeout="auto" unmountOnExit>
          {result && (
            <ConsoleOutput
              result={result}
              onClose={() => {
                setOpen(false);
                reset();
              }}
            />
          )}
        </Collapse>
      </Box>
    </Box>
  );
}

function ConsoleOutput({ result, onClose }: { result: RunResult; onClose: () => void }) {
  const ok = result.exitCode === 0;
  const statusColor: 'success' | 'warning' | 'error' = ok ? 'success' : result.stopped ? 'warning' : 'error';
  const statusLabel = ok
    ? 'Başarılı (exit 0)'
    : result.stopped
      ? 'Durduruldu'
      : result.exitCode === null
        ? 'Sonlandı'
        : `Çıkış kodu ${result.exitCode}`;

  return (
    <Box sx={{ mt: 1.5, borderRadius: 2, overflow: 'hidden', border: 1, borderColor: 'divider' }}>
      <Stack
        direction="row"
        alignItems="center"
        spacing={1}
        sx={{ px: 1.5, py: 1, bgcolor: 'rgba(0,0,0,0.6)', color: 'grey.100' }}
      >
        <TerminalRoundedIcon fontSize="small" />
        <Typography variant="body2" sx={{ fontFamily: '"JetBrains Mono", monospace', flexShrink: 0 }}>
          {result.command || 'çıktı'}
        </Typography>
        <Box sx={{ flexGrow: 1 }} />
        <Chip size="small" color={statusColor} label={statusLabel} />
        <Chip
          size="small"
          variant="outlined"
          sx={{ color: 'grey.300', borderColor: 'grey.600' }}
          label={`${(result.durationMs / 1000).toFixed(1)} sn`}
        />
        <Tooltip title="Kapat">
          <IconButton size="small" onClick={onClose} sx={{ color: 'grey.300' }}>
            <CloseRoundedIcon fontSize="small" />
          </IconButton>
        </Tooltip>
      </Stack>

      <Box
        component="pre"
        sx={{
          m: 0,
          p: 2,
          maxHeight: 460,
          overflow: 'auto',
          bgcolor: '#0b0b0f',
          color: '#e6e6e6',
          fontFamily: '"JetBrains Mono", monospace',
          fontSize: '0.8rem',
          lineHeight: 1.6,
          whiteSpace: 'pre-wrap',
          wordBreak: 'break-word',
        }}
      >
        {result.output || '(çıktı yok)'}
      </Box>

      {result.note && (
        <Alert severity={result.stopped ? 'warning' : 'info'} sx={{ borderRadius: 0 }}>
          {result.note}
        </Alert>
      )}
    </Box>
  );
}

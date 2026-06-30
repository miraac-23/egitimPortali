import { memo, useMemo } from 'react';
import ReactMarkdown, { type Components } from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { Box, Divider, Link, Tooltip, Typography } from '@mui/material';
import CodeBlock from './CodeBlock';

// README markdown'ını MUI tipografisiyle render eder.
// Fenced kod blokları CodeBlock ile syntax highlight edilir.
// onOpenCodeFile verilirse, yerel ".java" bağlantıları ilgili kod sekmesine geçer.
function buildComponents(onOpenCodeFile?: (fileName: string) => void): Components {
  return {
    h1: ({ children }) => (
      <Typography variant="h4" sx={{ mt: 1, mb: 2 }}>
        {children}
      </Typography>
    ),
    h2: ({ children }) => (
      <>
        <Divider sx={{ my: 3 }} />
        <Typography variant="h5" sx={{ mb: 1.5 }}>
          {children}
        </Typography>
      </>
    ),
    h3: ({ children }) => (
      <Typography variant="h6" sx={{ mt: 2.5, mb: 1 }}>
        {children}
      </Typography>
    ),
    h4: ({ children }) => (
      <Typography variant="subtitle1" sx={{ mt: 2, mb: 0.5, fontWeight: 700 }}>
        {children}
      </Typography>
    ),
    p: ({ children }) => (
      <Typography variant="body1" sx={{ my: 1.25, lineHeight: 1.75 }}>
        {children}
      </Typography>
    ),
    a: ({ href, children }) => {
      const url = (href ?? '').trim();
      // Mutlak (http/mailto), sayfa içi (#) ve kök (/) bağlantıları olduğu gibi bırak.
      const isAbsolute = /^(https?:|mailto:|tel:|#|\/)/i.test(url);

      // Yerel ".java" örnek dosyası -> ilgili kod sekmesine geç (yeni sekme/404 yok).
      const javaMatch = /([A-Za-z0-9_$]+\.java)(?:[?#].*)?$/.exec(url);
      if (!isAbsolute && javaMatch && onOpenCodeFile) {
        const fileName = javaMatch[1];
        return (
          <Tooltip title={`${fileName} kod sekmesini aç`}>
            <Link
              component="button"
              type="button"
              underline="hover"
              onClick={() => onOpenCodeFile(fileName)}
              sx={{ verticalAlign: 'baseline', cursor: 'pointer', font: 'inherit' }}
            >
              {children}
            </Link>
          </Tooltip>
        );
      }

      // Diğer göreli bağlantılar (başka README/klasör) -> kırık SPA navigasyonunu önle.
      if (!isAbsolute) {
        return (
          <Tooltip title={`Yerel bağlantı: ${url}`}>
            <Box component="span" sx={{ fontStyle: 'italic', textDecoration: 'underline dotted', cursor: 'help' }}>
              {children}
            </Box>
          </Tooltip>
        );
      }

      return (
        <Link href={url} target="_blank" rel="noopener noreferrer" underline="hover">
          {children}
        </Link>
      );
    },
    ul: ({ children }) => (
      <Box component="ul" sx={{ pl: 3, my: 1 }}>
        {children}
      </Box>
    ),
    ol: ({ children }) => (
      <Box component="ol" sx={{ pl: 3, my: 1 }}>
        {children}
      </Box>
    ),
    li: ({ children }) => (
      <Typography component="li" variant="body1" sx={{ my: 0.5, lineHeight: 1.7 }}>
        {children}
      </Typography>
    ),
    blockquote: ({ children }) => (
      <Box
        sx={{
          borderLeft: 4,
          borderColor: 'primary.main',
          pl: 2,
          py: 0.5,
          my: 2,
          bgcolor: 'action.hover',
          borderRadius: 1,
        }}
      >
        {children}
      </Box>
    ),
    table: ({ children }) => (
      <Box sx={{ overflowX: 'auto', my: 2 }}>
        <Box
          component="table"
          sx={{
            borderCollapse: 'collapse',
            width: '100%',
            '& td, & th': { border: 1, borderColor: 'divider', px: 1.5, py: 1, textAlign: 'left' },
            '& th': { bgcolor: 'action.hover' },
          }}
        >
          {children}
        </Box>
      </Box>
    ),
    code: ({ className, children }) => {
      const match = /language-(\w+)/.exec(className ?? '');
      const text = String(children ?? '').replace(/\n$/, '');
      const isInline = !className && !text.includes('\n');
      if (isInline) {
        return (
          <Box
            component="code"
            sx={{
              px: 0.7,
              py: 0.2,
              mx: 0.2,
              borderRadius: 1,
              bgcolor: 'action.selected',
              fontFamily: '"JetBrains Mono", monospace',
              fontSize: '0.85em',
            }}
          >
            {children}
          </Box>
        );
      }
      return (
        <Box sx={{ my: 2 }}>
          <CodeBlock code={text} language={match ? match[1] : 'text'} showLineNumbers={false} />
        </Box>
      );
    },
  };
}

interface MarkdownProps {
  children: string;
  onOpenCodeFile?: (fileName: string) => void;
}

function MarkdownInner({ children, onOpenCodeFile }: MarkdownProps) {
  const components = useMemo(() => buildComponents(onOpenCodeFile), [onOpenCodeFile]);
  return (
    <ReactMarkdown remarkPlugins={[remarkGfm]} components={components}>
      {children}
    </ReactMarkdown>
  );
}

export default memo(MarkdownInner);

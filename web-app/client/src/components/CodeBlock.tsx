import { useState } from 'react';
import { Box, IconButton, Tooltip, useTheme } from '@mui/material';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import CheckIcon from '@mui/icons-material/Check';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneDark, oneLight } from 'react-syntax-highlighter/dist/esm/styles/prism';

interface CodeBlockProps {
  code: string;
  language?: string;
  showLineNumbers?: boolean;
}

// Tek bir kod parçasını syntax highlight ile gösterir + sağ üstte kopyala butonu.
export default function CodeBlock({ code, language = 'java', showLineNumbers = true }: CodeBlockProps) {
  const theme = useTheme();
  const [copied, setCopied] = useState(false);
  const isDark = theme.palette.mode === 'dark';

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(code);
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
    } catch {
      /* pano erişimi yoksa sessizce yut */
    }
  };

  return (
    <Box sx={{ position: 'relative', '&:hover .copy-btn': { opacity: 1 } }}>
      <Tooltip title={copied ? 'Kopyalandı' : 'Kopyala'}>
        <IconButton
          className="copy-btn"
          size="small"
          onClick={handleCopy}
          sx={{
            position: 'absolute',
            top: 8,
            right: 8,
            zIndex: 1,
            opacity: { xs: 1, md: 0.4 },
            transition: 'opacity 0.2s',
            bgcolor: 'background.paper',
            border: 1,
            borderColor: 'divider',
          }}
        >
          {copied ? <CheckIcon fontSize="small" color="success" /> : <ContentCopyIcon fontSize="small" />}
        </IconButton>
      </Tooltip>
      <SyntaxHighlighter
        language={language}
        style={isDark ? oneDark : oneLight}
        showLineNumbers={showLineNumbers}
        wrapLongLines={false}
        customStyle={{
          margin: 0,
          borderRadius: 12,
          fontSize: '0.82rem',
          padding: '1rem',
          border: `1px solid ${theme.palette.divider}`,
          background: isDark ? '#0f0d14' : '#fafafa',
        }}
        codeTagProps={{ style: { fontFamily: '"JetBrains Mono", monospace' } }}
      >
        {code}
      </SyntaxHighlighter>
    </Box>
  );
}

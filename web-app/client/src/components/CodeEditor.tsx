import { Box } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import Editor from 'react-simple-code-editor';
import Prism from 'prismjs';
import 'prismjs/components/prism-java';

interface CodeEditorProps {
  value: string;
  onChange: (next: string) => void;
  language?: string;
  minHeight?: number;
  maxHeight?: number;
}

/**
 * IDE tarzı, canlı syntax-highlight'lı kod düzenleyici.
 * - Sol tarafta satır numarası cetveli (içerikle birlikte kayar)
 * - Dikey kaydırma DIŞ sarmalayıcıda yapılır (editörün kendi container'ı
 *   inline `overflow:hidden` taşır; bu yüzden scroll'u ona vermek çalışmaz)
 * - JetBrains Mono, Tab=4 boşluk, açık/koyu temaya uyumlu token renkleri
 * - Alt kenardan dikey olarak yeniden boyutlandırılabilir
 */
export default function CodeEditor({
  value,
  onChange,
  language = 'java',
  minHeight = 340,
  maxHeight = 620,
}: CodeEditorProps) {
  const theme = useTheme();
  const isDark = theme.palette.mode === 'dark';

  // Her satırın başına, gutter'a oturacak bir satır-numarası span'i ekler.
  const highlightWithLines = (code: string) => {
    const grammar = Prism.languages[language] ?? Prism.languages.java;
    return Prism.highlight(code, grammar, language)
      .split('\n')
      .map((line, i) => `<span class="cm-ln">${i + 1}</span>${line || ' '}`)
      .join('\n');
  };

  // IDE renk paleti (One Dark / One Light esinli).
  const c = isDark
    ? {
        bg: '#0d1117',
        gutterBg: '#0a0d12',
        gutterLine: 'rgba(255,255,255,0.07)',
        text: '#c9d1d9',
        lineNo: '#4b5563',
        caret: '#58a6ff',
        selection: 'rgba(56,139,253,0.30)',
        comment: '#6a737d',
        keyword: '#ff7b72',
        string: '#a5d6ff',
        number: '#79c0ff',
        function: '#d2a8ff',
        classname: '#ffa657',
        annotation: '#7ee787',
        punctuation: '#8b949e',
      }
    : {
        bg: '#fbfbfd',
        gutterBg: '#f1f1f6',
        gutterLine: 'rgba(0,0,0,0.08)',
        text: '#24292e',
        lineNo: '#b0b7c3',
        caret: '#6750A4',
        selection: 'rgba(103,80,164,0.18)',
        comment: '#9ca3af',
        keyword: '#cf222e',
        string: '#0a7d33',
        number: '#0550ae',
        function: '#8250df',
        classname: '#953800',
        annotation: '#116329',
        punctuation: '#6e7781',
      };

  // Gutter şeridi + 1px ayraç: içerikle birlikte kaydığı için tüm yükseklikte görünür.
  const gutterBackground =
    `linear-gradient(90deg, ${c.gutterBg} 0 3.2rem, ${c.gutterLine} 3.2rem calc(3.2rem + 1px), ${c.bg} calc(3.2rem + 1px))`;

  return (
    <Box
      // DIŞ SARMALAYICI = kaydırma alanı
      sx={{
        position: 'relative',
        borderRadius: 2.5,
        border: 1,
        borderColor: isDark ? 'rgba(255,255,255,0.10)' : 'rgba(0,0,0,0.12)',
        bgcolor: c.bg,
        minHeight,
        maxHeight,
        overflow: 'auto',
        resize: 'vertical',
        // İnce, modern kaydırma çubuğu
        '&::-webkit-scrollbar': { width: 10, height: 10 },
        '&::-webkit-scrollbar-thumb': {
          borderRadius: 8,
          bgcolor: isDark ? 'rgba(255,255,255,0.18)' : 'rgba(0,0,0,0.18)',
          border: '2px solid transparent',
          backgroundClip: 'padding-box',
        },
        // react-simple-code-editor container'ı: içeriğe göre büyür, gutter'ı boyar
        '& .cm-editor': {
          minHeight,
          background: gutterBackground,
          fontFamily: '"JetBrains Mono", monospace',
          fontSize: '0.82rem',
          lineHeight: 1.65,
        },
        // Textarea: metni şeffaf (altındaki renklendirilmiş <pre> görünür), yalnızca imleç renkli.
        '& .cm-editor textarea': {
          outline: 'none !important',
          caretColor: c.caret,
          color: 'transparent !important',
          WebkitTextFillColor: 'transparent',
        },
        '& .cm-editor pre': { color: c.text },
        '& .cm-editor textarea, & .cm-editor pre': {
          paddingLeft: '4rem !important',
        },
        '& .cm-editor textarea::selection': { background: c.selection },
        // Satır numarası span'i: gutter'a hizalı, sağa dayalı.
        '& .cm-ln': {
          position: 'absolute',
          left: 0,
          width: '2.7rem',
          textAlign: 'right',
          color: c.lineNo,
          userSelect: 'none',
          pointerEvents: 'none',
        },
        // Prism token renkleri
        '& .token.comment, & .token.prolog, & .token.doctype, & .token.cdata': {
          color: c.comment,
          fontStyle: 'italic',
        },
        '& .token.punctuation': { color: c.punctuation },
        '& .token.keyword, & .token.boolean, & .token.null': { color: c.keyword },
        '& .token.string, & .token.char': { color: c.string },
        '& .token.number': { color: c.number },
        '& .token.function': { color: c.function },
        '& .token.class-name': { color: c.classname },
        '& .token.annotation': { color: c.annotation },
        '& .token.operator': { color: c.punctuation, background: 'transparent' },
      }}
    >
      <Editor
        value={value}
        onValueChange={onChange}
        highlight={highlightWithLines}
        padding={14}
        tabSize={4}
        insertSpaces
        textareaClassName="cm-textarea"
        className="cm-editor"
        style={{ fontFamily: '"JetBrains Mono", monospace', fontSize: '0.82rem' }}
      />
    </Box>
  );
}

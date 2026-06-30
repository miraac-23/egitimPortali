// Backend ContentModels ile birebir eşleşen tipler.

export interface Category {
  id: string; // "01-java"
  title: string; // "Java"
  description: string;
  order: number;
  topicCount: number;
}

export interface CodeLevel {
  key: string; // dosya adı (uzantısız): "Ornek1"
  label: string; // "Örnek 1"
  fileName: string; // "Ornek1.java"
}

export interface TopicSummary {
  id: string; // "01-java/05-concurrency-temelleri"
  category: string;
  categoryTitle: string;
  slug: string;
  title: string;
  summary: string;
  order: number;
  levels: CodeLevel[];
}

export interface CodeFile {
  key: string;
  label: string;
  fileName: string;
  language: string;
  lineCount: number;
  content: string;
}

export interface TopicDetail {
  id: string;
  category: string;
  categoryTitle: string;
  slug: string;
  title: string;
  summary: string;
  order: number;
  readme: string;
  codeFiles: CodeFile[];
}

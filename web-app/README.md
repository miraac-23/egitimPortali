# Eğitim Portalı — Web Uygulaması (UI + API)

Bu klasör, depodaki **Java & Spring eğitim çalışmasını** interaktif bir web
uygulamasıyla görselleştirir. İki bölümden oluşur:

| Klasör | Teknoloji | Görev |
|--------|-----------|-------|
| [`server/`](server/) | **Spring Boot 3.3 / Java 21** | Eğitim içeriğini (README + örnek kodlar) dosya sisteminden okuyup REST API olarak sunar **+** canlı demo için Görev (Task) CRUD API'si. |
| [`client/`](client/) | **React 18 + TypeScript + Redux Toolkit + RTK Query + MUI v7 + Vite** | Responsive, modern arayüz: konu gezgini, markdown anlatım, syntax-highlight kod görüntüleyici, canlı API demosu. |

```
Tarayıcı ─▶ React (Vite, :5173) ─/api,/actuator (proxy)─▶ Portal API (:8085) ─▶ Depo içeriği + H2
                                                                  │
                                                                  └─ "Çalıştır" ─▶ örnek kodu derleyip çalıştırır
                                                                       • tek-dosya Java: java Dosya.java
                                                                       • Spring Boot: ornekler modülü, :8080
```

> **Portlar:** Portal API **:8085**'te çalışır. **:8080**, çalıştırılan **Spring Boot
> örneklerine** ayrılmıştır (örnekler kendilerini `http://localhost:8080` üzerinden
> çağırır), bu yüzden portal kasıtlı olarak 8085'tedir.

---

## Önkoşullar

| Araç | Sürüm | Kontrol |
|------|-------|---------|
| JDK  | 21    | `java -version` |
| Node | 20+   | `node -v` |
| npm  | 10+   | `npm -v` |

> Bu depo JDK 21 (Temurin), Node 20, npm 10 ile test edilmiştir. Gradle, wrapper
> (`./gradlew`) ile otomatik indirilir — ayrıca Gradle kurmaya gerek yoktur.

---

## Çalıştırma

İki ayrı terminal kullanın.

### 1) Backend (Spring Boot)

```bash
cd web-app/server
./gradlew bootRun
```

- API: <http://localhost:8085>
- Health: <http://localhost:8085/actuator/health>
- H2 konsolu: <http://localhost:8085/h2-console> (JDBC URL: `jdbc:h2:mem:portaldb`)

> Sunucu, eğitim içeriğini bulmak için çalışma dizininden yukarı doğru
> `01-java` + `02-spring` klasörlerini içeren **depo kökünü** otomatik arar.
> Farklı bir konumdan çalıştıracaksanız `application.yml` içindeki
> `egitim.content.root` değerini verin.

### 2) Frontend (React + Vite)

```bash
cd web-app/client
npm install      # ilk kez
npm run dev
```

- Uygulama: <http://localhost:5173>

Vite dev sunucusu `/api` ve `/actuator` isteklerini otomatik olarak `:8080`'e
proxy'ler; bu yüzden tarayıcıda CORS sorunu yaşanmaz. (Backend tarafında CORS
da ayrıca yapılandırılmıştır — bkz. `WebConfig`.)

---

## API Uçları

### İçerik API'si (`server` → dosya sistemi)

| Metot | Yol | Açıklama |
|-------|-----|----------|
| GET | `/api/content/categories` | Üst kategoriler (Java, Spring) + konu sayıları |
| GET | `/api/content/topics` | Tüm konular (`?category=01-java`, `?q=arama`) |
| GET | `/api/content/topics/{category}/{slug}` | Konu detayı: README + kod dosyaları |

### Kod Çalıştırma API'si (`server` → JDK / Gradle)

| Metot | Yol | Açıklama |
|-------|-----|----------|
| POST | `/api/run` | Body: `{ category, slug, level }` (level: `TEMEL`/`ORTA`/`ILERI`). Örneği çalıştırıp çıktısını döndürür. |

- **Tek-dosya Java** (`01-java/*`, `02-spring/00–03`): `java Dosya.java` ile çalışır, çıktı basıp biter (~saniyeler).
- **Spring Boot** (`02-spring/04–14`): `ornekler` modülünde `bootRun -Papp=<fqcn>` ile başlar; "Started"
  görülünce self-test çıktısı alınıp **otomatik durdurulur** (sunucu açık kalmaz). Aynı anda yalnızca bir
  Spring örneği çalışır (8080 kilidi). İlk çalıştırmada Gradle bağımlılık indirebilir.
- Güvenlik: İstemci ham kod göndermez; yalnızca depodaki bilinen örnek dosyaları (category/slug/level) çalıştırılır.

### Canlı Demo — Görev API'si (`server` → H2)

| Metot | Yol | Açıklama |
|-------|-----|----------|
| GET | `/api/tasks` | Görev listesi (`?status=TODO\|IN_PROGRESS\|DONE`) |
| GET | `/api/tasks/{id}` | Tek görev |
| POST | `/api/tasks` | Görev oluştur (`@Valid`) |
| PUT | `/api/tasks/{id}` | Görev güncelle |
| DELETE | `/api/tasks/{id}` | Görev sil |

---

## Üretim derlemesi

```bash
cd web-app/client && npm run build     # -> dist/ (statik dosyalar)
cd web-app/server && ./gradlew build   # -> build/libs/*.jar
```

`dist/` çıktısı herhangi bir statik sunucudan; jar ise `java -jar` ile
servis edilebilir. (İsteğe bağlı: `dist/` içeriğini `server/src/main/resources/static/`
altına kopyalayıp tek bir Spring Boot jar'ı olarak da dağıtabilirsiniz.)

---

## İstemci mimarisi (özet)

```
src/
├── app/            store.ts (Redux), baseApi.ts (RTK Query), hooks.ts
├── features/
│   ├── content/    contentApi.ts (içerik endpoint'leri) + types
│   ├── tasks/      tasksApi.ts (CRUD) + TaskFormDialog + types
│   ├── runner/     runnerApi.ts (/api/run) + RunPanel (Çalıştır + konsol çıktısı)
│   └── ui/         uiSlice.ts (tema modu, drawer)
├── components/     Layout, Sidebar, Markdown, CodeBlock, ApiStatusIndicator
├── pages/          HomePage, TopicPage, TasksPage, SearchPage, NotFoundPage
└── theme.ts        açık/koyu MUI teması
```

- **RTK Query**: Tüm HTTP çağrıları; mutasyon sonrası tag invalidation ile
  liste otomatik tazelenir.
- **Tema**: `ui` slice'ında tutulur, `localStorage`'a yazılır, sistem tercihini okur.
- **Kod görüntüleyici**: `react-syntax-highlighter` (Prism) + kopyala butonu.
- **Markdown**: `react-markdown` + `remark-gfm`, MUI tipografisiyle render edilir.

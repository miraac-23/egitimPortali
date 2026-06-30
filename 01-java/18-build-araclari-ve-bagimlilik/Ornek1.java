// Ornek1: Semantic Versioning (SemVer) mantığını modelleyen küçük bir program.
// Build araçları bağımlılıkları "1.2.3" gibi sürümlerle yönetir; bu örnek o mantığı gösterir.
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    // MAJOR.MINOR.PATCH — anlamsal sürümleme.
    record Surum(int major, int minor, int patch) implements Comparable<Surum> {
        static Surum ayristir(String s) {
            String[] p = s.split("\\.");
            return new Surum(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
        }

        @Override public int compareTo(Surum o) {
            if (major != o.major) return Integer.compare(major, o.major);
            if (minor != o.minor) return Integer.compare(minor, o.minor);
            return Integer.compare(patch, o.patch);
        }

        // "^1.2.3" kuralı: aynı MAJOR içindeki, >= bu sürüm olanlar uyumludur.
        boolean uyumlu(Surum diger) {
            return diger.major == this.major && diger.compareTo(this) >= 0;
        }

        @Override public String toString() { return major + "." + minor + "." + patch; }
    }

    public static void main(String[] args) {
        Surum istenen = Surum.ayristir("1.2.0");
        System.out.println("İstenen sürüm (^" + istenen + "):\n");

        String[] adaylar = {"1.2.0", "1.4.5", "1.9.9", "2.0.0", "1.1.0"};
        for (String a : adaylar) {
            Surum s = Surum.ayristir(a);
            System.out.printf("  %-7s -> %s%n", s, istenen.uyumlu(s) ? "uyumlu" : "UYUMSUZ");
        }

        System.out.println("\nKural: MAJOR değişince geriye dönük uyumluluk KIRILIR.");
        System.out.println("Bu yüzden build araçları sürüm aralıklarını dikkatle çözer.");
    }
}

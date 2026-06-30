# Eureka — Servis Keşfi (Service Discovery)

> ⚠️ **Bu konuda çalıştırılabilir örnek yoktur.** Eureka, **Spring Cloud** bağımlılığı ve birden
> çok ayrı servis gerektirir; bu portal (tek JVM, JDK 21) bunu çalıştıramaz. Aşağıdaki kod/yapı
> tanıtım amaçlıdır.

Mikroservis mimarisinde onlarca, yüzlerce servis vardır ve bunlar **dinamik** olarak ölçeklenir
(yeni örnekler açılır/kapanır, IP/port değişir). Bir servis başka bir servisi nasıl bulur?
Adresleri elle (hard-coded) yazmak imkânsızdır. Çözüm **servis keşfi (service discovery)**:
servisler bir **kayıt defterine (registry)** kaydolur; diğerleri o defterden adresi sorar.
**Netflix Eureka**, Spring Cloud'un en yaygın servis keşif çözümüdür.

## Sorun: servisler birbirini nasıl bulur?

```
Sipariş Servisi  ->  Ödeme Servisi'ni çağırmak istiyor
                     ama Ödeme Servisi'nin adresi ne? (192.168.x.x:????)
                     - 3 örneği var, hangisi? - biri çöktü, yenisi açıldı...
```

Statik adres yazmak kırılgandır. Servis keşfi bunu çözer: "Ödeme Servisi nerede?" sorusunu bir
registry'ye sorarsın, o da güncel adres(ler)i verir.

## Eureka Server (kayıt defteri)

Eureka Server, tüm servislerin kaydolduğu merkezi defterdir. Kurulumu çok basittir:

```gradle
// build.gradle
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-server'
```

```java
@SpringBootApplication
@EnableEurekaServer            // bu uygulamayı bir Eureka kayıt sunucusu yapar
public class EurekaServerApp {
    public static void main(String[] args) { SpringApplication.run(EurekaServerApp.class, args); }
}
```

```yaml
# application.yml
server.port: 8761                       # Eureka'nın standart portu
eureka:
  client:
    register-with-eureka: false         # sunucunun kendisi kaydolmaz
    fetch-registry: false
```

`http://localhost:8761` adresinde Eureka'nın web panosu açılır; kayıtlı servisleri görürsün.

## Nasıl çalışır?

1. **Kayıt (register):** Her servis açılınca Eureka'ya "ben Ödeme Servisi'yim, adresim şu" der.
2. **Kalp atışı (heartbeat):** Servisler düzenli "hâlâ ayaktayım" sinyali gönderir; göndermezse
   Eureka onları defterden düşürür (sağlık takibi).
3. **Keşif (discovery):** Bir servis başkasını çağıracağında, IP değil **servis adıyla** sorar;
   Eureka güncel adresi döndürür.
4. **Yük dengeleme:** Bir servisin birden çok örneği varsa, istemci tarafı yük dengeleme
   (client-side load balancing) ile aralarında dağıtım yapılır.

## Eureka mimarideki yeri

```
        ┌──────────────── Eureka Server (8761) ────────────────┐
        │   kayıt defteri: SIPARIS->[...], ODEME->[...], ...     │
        └───────────▲───────────────▲───────────────▲───────────┘
            kaydol   │       kaydol   │       kaydol   │  (heartbeat)
        ┌────────────┴──┐  ┌──────────┴───┐  ┌─────────┴────┐
        │ Sipariş Servisi│  │ Ödeme Servisi│  │ Kargo Servisi│
        └────────────────┘  └──────────────┘  └──────────────┘
```

Servisleri Eureka'ya **kaydetme** ve birbirini **bulup çağırma** ayrı bir konudur (sonraki:
Service Registration).

## Modern bağlam ve alternatifler

- **Spring Cloud Netflix** (Eureka dahil) bakım modundadır ama Eureka hâlâ yaygındır.
- **Kubernetes** ortamlarında servis keşfi genelde **platform tarafından** sağlanır (K8s Service +
  DNS); ayrı bir Eureka gerekmez.
- Alternatifler: **Consul**, **etcd**, Spring Cloud LoadBalancer.

## Özet

Servis keşfinin, dinamik mikroservis ortamında servislerin birbirini **adres yazmadan** bulmasını
sağladığını; **Eureka Server**'ın merkezi kayıt defteri olarak kurulumunu (`@EnableEurekaServer`,
port 8761) ve çalışma mantığını (kayıt → heartbeat → keşif → yük dengeleme) öğrendik; Kubernetes/
Consul alternatiflerine değindik. Sırada, servisleri bu deftere kaydedip birbirini çağırma:
**Service Registration**.

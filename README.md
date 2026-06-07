# TicketApp

**TicketApp**, Android için modüler bir bilet yönetim uygulamasıdır. Bu proje:

- `app` modülü ile kullanıcı arayüzünü ve uygulama akışını oluşturur
- `core` modülü ile bağımlılık olmayan domain modellerini, repository arayüzlerini ve ortak UI/yardımcı yapılarını içerir
- `data` modülü ile API, veri erişimi, token yönetimi ve uygulama dışı katmanla iletişimi sağlar

Proje aynı zamanda:
- Jetpack Compose ile modern UI
- Koin ile bağımlılık yönetimi
- Retrofit / OkHttp ile REST API iletişimi
- DataStore ile token saklama
- MVVM + StateFlow ile reaktif durum yönetimi
- Compose Navigation ile ekranlar arası yönlendirme
- QR kod ve bilet kontrol senaryoları için ZXing destekli kütüphaneleri içerir

---


### 1. Root Dosyaları

- `settings.gradle.kts`
  - Projedeki modülleri belirler: `:app`, `:core`, `:data`
  - Gradle plugin yönetimi ve bağımlılık çözümleme için repository yapılandırmasını içerir

- `build.gradle.kts`
  - Ortak Gradle eklentilerini alias olarak tanımlar
  - Alt modüllerin bu eklentileri kullanmasını sağlar

- `gradle/libs.versions.toml`
  - Proje bağımlılık versiyonlarını merkezi olarak tutar
  - `app`, `core`, `data` modülleri bu versiyon kataloğuna referans verir

---

## 2. `app` Modülü

`app` modülü, uygulamanın runtime tarafını ve kullanıcı arayüzünü barındırır.



### 2.1 Dependecy Injection

- `app/src/main/java/com/turkcell/ticketapp/di/appModule.kt`
  - ViewModel sınıflarını Koin `viewModelOf(::XyzViewModel)` şeklinde kayıt eder
  - `LoginViewModel`, `RegisterViewModel`, `HomeViewModel`, `EventDetailViewModel`,
    `PurchaseViewModel`, `MyTicketViewModel`, `TicketDetailViewModel`, `CheckinViewModel`
    burada tanımlanır

### 2.3 Navigasyon

- `app/src/main/java/com/turkcell/ticketapp/navigation/AppNavHost.kt`
  - Uygulamanın auth durumuna göre yönlendirme yapar
  - `AuthRepository.isLoggedIn` akışını izleyerek:
    - `null` ise splash yükleme ekranı
    - `true` ise yetkili navigasyon
    - `false` ise login/register akışı
  - `AuthedNavHost` ile `Home`, `EventDetail`, `MyTickets`, `TicketDetail`, `Staff`
    ekranlarını yönetir

- `app/src/main/java/com/turkcell/ticketapp/navigation/AppDestinations.kt`
  - Her ekran için serializable hedef nesneleri tanımlar
  - Ekranlar arasında navigasyon sağlar

### 2.4 Ekranlar

`app/src/main/java/com/turkcell/ticketapp/screen/`

- `LoginScreen.kt`
  - Email ve şifre girişi
  - Şifre görünürlüğünü aç/kapat
  - Login işlemini tetikler
  - Hata mesajlarını `Snackbar` ile gösterir

- `RegisterScreen.kt`
  - Yeni kullanıcı kaydı için form
  - Kayıt tamamlandığında `Home` ekranına yönlendirir

- `HomeScreen.kt`
  - Etkinlik ve bilet listelerini gösterir
  - Kullanıcı rolüne göre `Staff` erişimi
  - Event / Ticket detay sayfalarına geçiş sağlar

- `EventDetailScreen.kt`
  - Seçilen etkinliğin detaylarını gösterir
  - Bilet tipi seçimi ve adet artırma/azaltma
  - Satın alma akışını başlatır

- `PurchaseConfirmDialog.kt`
  - Satın alma işlemini onaylamak için modal dialog
  - Ödeme sonrası yönlendirme mantığı içerir

- `MyTicketsScreen.kt`
  - Kullanıcının sahip olduğu biletleri listeler
  - Bilet detayına geçiş sağlar

- `TicketDetailScreen.kt`
  - Seçilen biletin detaylarını gösterir

- `StaffScreen.kt`
  - Görevli personel için bilet kontrol/okuma senaryosu içerir
  - ZXing kütüphaneleri QR kod tarama için kullanılır

### 2.5 ViewModel Katmanı

`app/src/main/java/com/turkcell/ticketapp/viewmodel/`

Her ViewModel:
- UI durumunu `StateFlow` ile yönetir
- Repository arayüzlerini kullanarak veri çeker
- Hata yönetimini `toUserMessage()` ile kullanıcıya uygun hale getirir
- `viewModelScope.launch { ... }` ile coroutine tabanlı async işlemler yapar

- `LoginViewModel.kt` → giriş işlemi, form validasyonu, hataların yönetimi
- `RegisterViewModel.kt` → yeni kullanıcı kaydı
- `HomeViewModel.kt` → etkinlik ve bilet yükleme, refresh, kullanıcı rolü gözlemi
- `EventDetailViewModel.kt` → tek etkinlik detayları, bilet miktarı seçimi
- `PurchaseViewModel.kt` → satın alma oluşturma ve ödeme onayı
- `MyTicketViewModel.kt` → kullanıcının bilet listesini yükleme
- `TicketDetailViewModel.kt` → tek bilet detayları
- `CheckinViewModel.kt` → personel veya check-in senaryosunun yönetimi



---

## 3. `core` Modülü

`core` modülü, uygulamanın bağımlılıklardan bağımsız domain tanımlarını taşır.



### 3.1 Domain ve Arayüzler

`core/src/main/java/com/turkcell/core/domain/`

- `auth/`
  - `AuthRepository.kt` → giriş, kayıt, çıkış, oturum durumu, kullanıcı rolü için sözleşme
  - `AuthSession.kt`, `User.kt`, `UserRole.kt` → kimlik bilgileri ve roller

- `event/`
  - `EventRepository.kt` → etkinlik listesini ve tek etkinliği tanımlar
  - `Event.kt`, `TicketType.kt` → etkinlik modeli ve bilet tipi modeli

- `ticket/`
  - `TicketRepository.kt` → bilet sorgulama sözleşmesi
  - `Ticket.kt`, `TicketStatus.kt` → biletin durum bilgisi

- `purchase/`
  - `PurchaseRepository.kt` → satın alma oluşturma ve ödeme onayı
  - `PurchaseDomain.kt` → satın alma için domain modeli

- `checkin/`
  - `CheckinRepository.kt` → bilet check-in / personel doğrulama mantığı

### 3.2 Ortak Yardımcılar

- `core/exception/`
  - `ApiException`, `NetworkException` gibi hata tipleri

- `core/util/ErrorMessages.kt`
  - `Throwable.toUserMessage()`
  - Hataları kullanıcı dostu mesajlara çevirir
  - Örnekler: `401`, `403`, `404`, `409`, `500+`, ağ hatası

- `core/ui/`
  - Tema ve ortak UI bileşenleri bulunduğu klasör



---

## 4. `data` Modülü

`data` modülü, `core` içinde tanımlanan repository arayüzlerinin gerçek uygulamasını sağlar.





### 4.1 Bağımlılık Modülü

- `data/src/main/java/com/turkcell/data/di/DataModule.kt`
  - `Json` serileştirici
  - OkHttp `HttpLoggingInterceptor`
  - `AuthInterceptor` ve `TokenAuthenticator`
  - `Retrofit` örnekleri
  - API arayüzleri (`AuthApi`, `EventApi`, `PurchaseApi`, `MeApi`, `CheckinApi`)
  - Repository implementasyonları (`AuthRepositoryImpl`, `EventRepositoryImpl`, `PurchaseRepositoryImpl`, `TicketRepositoryImpl`, `CheckinRepositoryImpl`)
  - `TokenStore` singleton olarak Koin içinde sağlanır

### 4.2 Veri Erişim Katmanı

- `data/remote/`
  - API son noktalarını tanımlayan Retrofit arayüzleri
  - Örnekler: `/auth/login`, `/auth/register`, `/auth/refresh`

- `data/local/`
  - `TokenStore.kt` ile erişim/refresh token ve kullanıcı rolü DataStore içinde saklanır
  - Flow tabanlı erişim sağlanır ve bloklayıcı yardımcı fonksiyonlar da içerir

- `data/repository/`
  - `AuthRepositoryImpl.kt`
    - API çağrılarını yapar
    - başarılı yanıt geldiğinde tokenları kaydeder
    - `Result` tipi ile hata yönetimi yapar
  - `EventRepositoryImpl`, `TicketRepositoryImpl`, `PurchaseRepositoryImpl`, `CheckinRepositoryImpl`
    - API yanıtlarını domain modellere dönüştürür
    - `runCatchingApi` yardımcı fonksiyonu ile güvenli çağrı yapar

- `data/dto/`
  - API request/response modelleri
  - Domain modellerine dönüştürülecek veri yapılarını içerir

- `data/mapper/`
  - DTO ve domain modeller arasında dönüşüm mantığı bulunur

- `data/util/`
  - API çağrılarını sararak hata yakalama ve `Result` dönüşü sağlama gibi yardımcı fonksiyonlar

### 4.3 Önemli Teknolojiler

- `Retrofit` + `Kotlinx Serialization`
- `OkHttp Interceptor` + `Authenticator`
- `DataStore` ile kalıcı token saklama
- `Koin` ile repository ve servislerin bağlanması

---

## 5. Önemli Dosyalar ve Görevleri

### `app/TicketAppApplication.kt`
Uygulama açıldığında Koin başlatılır.
- `androidContext(this)`
- `androidLogger()`
- `modules(dataModule, appModule)`

### `app/MainActivity.kt`
Uygulamanın Compose başlangıç noktasıdır.
- `enableEdgeToEdge()`
- `setContent { TicketAppTheme { KoinAndroidContext { AppNavHost() } } }`

### `app/navigation/AppNavHost.kt`
Auth durumuna göre koşullu yönlendirme sağlar.
- `SplashScreen`, `AuthedNavHost`, `UnAuthedNavHost`
- Login sonrası `Home` ekranına geçiş
- `popUpTo` ile geri yığın yönetimi

### `app/screen/LoginScreen.kt`
Giriş ekranı UI bileşeni.
- `OutlinedTextField` email ve şifre
- `Button` ile giriş tetikleme
- `SnackbarHost` ile hata gösterimi

### `app/viewmodel/LoginViewModel.kt`
Form durumunu yönetir.
- `canSubmit` validasyonu
- `login(email, password)` çağrısı
- hata mesajlarına `toUserMessage()` uygulaması

### `core/domain/auth/AuthRepository.kt`
Auth iş mantığının sözleşmesini tanımlar.


### `data/local/TokenStore.kt`
Tokenları güvenli şekilde saklar.


### `data/di/DataModule.kt`
Ağ ve veri bağımlılıklarını bağlar.
- `AuthInterceptor` ile tüm isteklere access token ekleme
- `TokenAuthenticator` ile token refresh mekanizması
- `Retrofit` ve servis üretimi
- `AuthRepositoryImpl` vs.

### `core/util/ErrorMessages.kt`
Cihaz hatalarını kullanıcıya çevirir.
- `401`, `403`, `404`, `409`, `500+`
- ağ bağlantı hatası için özel mesaj

---



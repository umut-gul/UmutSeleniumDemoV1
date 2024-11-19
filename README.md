# Uçak Bileti Otomatik Test Süreci

Bu proje, `www.enuygun.com` sitesinin uçak bileti arama ve satın alma süreçlerini otomatik olarak test eden bir Selenium ve TestNG tabanlı test otomasyonudur. Proje, uçuş arama, filtreleme, Türk Hava Yolları uçuş fiyat sıralaması ve biletleme sürecini kapsamaktadır.

## Gereksinimler

- Java 17
- Maven
- ChromeDriver
- Bağımlılıklar (pom.xml dosyasındaki bağımlılıklar otomatik olarak yüklenecektir)

## Kurulum

Projeyi klonlayın:
git clone https://github.com/kullanici/UmutSeleniumDemoV1.git

Proje dizinine gidin:
cd proje-adi

Bağımlılıkları yükleyin:
mvn clean install

Test Senaryoları
TC01 - Uçuş Arama ve Zaman Filtreleme
Test Adımları:

İstanbul-Ankara uçuşlarını aratır.
Uçuşları 10:00 - 18:00 saat aralığında filtreler.
Filtrelenen uçuşların saatlerinin istenilen aralıkta olduğunu doğrular.
Çalıştırma:

mvn test -DsuiteXmlFile=src/test/java/tests/TC01.xml


TC02 - Türk Hava Yolları Fiyat Sıralaması Kontrolü
Test Adımları:

İstanbul-Ankara uçuşlarını aratır.
Uçuşları 10:00 - 18:00 saat aralığında filtreler.
Türk Hava Yolları uçuşlarını bulur ve fiyatların artan sırada olup olmadığını kontrol eder.
Çalıştırma:

mvn test -DsuiteXmlFile=src/test/java/tests/TC02.xml


TC03 - Biletleme Süreci ve 3D Secure Doğrulama
Test Adımları:

İstanbul-Ankara uçuşlarını aratır.
İlk uçuşu seçer.
Yolcu bilgilerini doldurur.
Ödeme bilgilerini girer ve 3D Secure ekranına geçer.
Çalıştırma:
mvn test -DsuiteXmlFile=src/test/java/tests/TC03.xml
Kullanım

Belirli bir test senaryosunu çalıştırmak için yukarıdaki komutlardan birini kullanabilirsiniz. Test sonuçları konsolda ve target/surefire-reports klasöründe HTML formatında ve test-output klasörü altında oluşturulacaktır.

Bu proje, uçak bileti arama ve biletleme süreçlerinde kullanıcı deneyimini iyileştirmek için otomatik testlerle kaliteyi artırmayı amaçlamaktadır.

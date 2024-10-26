#import chromedriver_autoinstaller
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
import json
import time
from concurrent.futures import ThreadPoolExecutor
from selenium.webdriver.chrome.options import Options

# Chromedriver'ı otomatik olarak yükle
#chromedriver_autoinstaller.install()

# Hisse senedi sembollerinin listesi (örnek)
stocks_list = ['AAPL', 'MSFT', 'GOOGL']  # Buraya 500 hisse senedinin sembollerini ekleyin

# Eski haber başlıklarını saklamak için bir sözlük
previous_titles = {symbol: None for symbol in stocks_list}

# Headless modda Chrome WebDriver ayarları
chrome_options = Options()
chrome_options.add_argument("--headless")  # Tarayıcıyı görünmez yapar
chrome_options.add_argument("--no-sandbox")  # Güvenlik engellerini atlar
chrome_options.add_argument("--disable-dev-shm-usage")  # Bellek sorunlarını önler

# Tek bir hisse senedi için haber kontrol fonksiyonu
def check_stock_news(symbol):
    try:
        # Chrome WebDriver'ı başlat
        service = Service()
        driver = webdriver.Chrome(service=service, options=chrome_options)

        # Hisse senedi haber sayfasına git
        driver.get(f'https://finance.yahoo.com/quote/{symbol}/news/')

        # Sayfanın yüklenmesini bekle
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.TAG_NAME, "h3"))
        )

        # Sayfa kaynağını (HTML) al
        html = driver.page_source
        driver.quit()

        # HTML'i BeautifulSoup ile parse et
        soup = BeautifulSoup(html, 'html.parser')

        # Son haberi bul
        latest_news = soup.find('li', class_=['stream-item', 'story-item'])
        if latest_news:
            latest_title = latest_news.find('h3').get_text() if latest_news.find('h3') else None
        else:
            print(f"{symbol}: Yeni haber bulunamadı.")
            return

        # Eğer yeni bir başlık varsa
        if latest_title and latest_title != previous_titles[symbol]:
            # Haber detay sayfasına git
            news_link_element = latest_news.find('a')

            if news_link_element and 'href' in news_link_element.attrs:
                news_link = news_link_element['href']
                if not news_link.startswith('http'):
                    news_link = 'https://finance.yahoo.com' + news_link

                # Haber detay sayfasına gitmek için yeni WebDriver başlat
                driver = webdriver.Chrome(service=service, options=chrome_options)
                driver.get(news_link)

                # Sayfa kaynağını al ve parse et
                html = driver.page_source
                driver.quit()

                soup = BeautifulSoup(html, 'html.parser')

                # Haberin detaylarını çıkar
                title = soup.find('h1', class_='cover-title')
                stocks = soup.find_all('span', class_=['symbol', 'yf-138ga19'])
                time_el = soup.find_all('time', class_='byline-attr-meta-time')
                paragraphs = soup.find_all('p', class_='yf-1pe5jgt')

                # JSON için veri hazırla
                data = {
                    "title": title.get_text() if title else None,
                    "stocks": [stock.get_text() for stock in stocks],
                    "time": time_el[0].get_text() if time_el else None,
                    "paragraphs": [paragraph.get_text() for paragraph in paragraphs]
                }

                # JSON dosyasına yaz (her yeni haber için)
                json_file_path = f"{symbol}_yeniHaber.json"
                with open(json_file_path, 'w', encoding='utf-8') as json_file:
                    json.dump(data, json_file, ensure_ascii=False, indent=4)

                print(f"{symbol}: Yeni haber bulundu: {latest_title} ve {json_file_path} dosyasına yazıldı.")

                # Son başlığı güncelle
                previous_titles[symbol] = latest_title
            else:
                print(f"{symbol}: Haber bağlantısı bulunamadı.")
        else:
            print(f"{symbol}: Yeni haber yok veya başlık aynı.")

    except Exception as e:
        print(f"{symbol} için hata: {e}")

# 10 saniyede bir tüm hisse senetlerini kontrol eden döngü
while True:
    with ThreadPoolExecutor(max_workers=10) as executor:
        executor.map(check_stock_news, stocks_list)

    # 10 saniye bekle ve tekrar kontrol et
    time.sleep(10)
package com.test.webscraper.njuskalo;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.test.webscraper.common.Item;
import com.test.webscraper.repository.HashRecord;
import com.test.webscraper.repository.HashRepository;
import com.test.webscraper.telegram.TelegramClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import static com.test.webscraper.common.CharHelper.cleanWierdLetters;
import static com.test.webscraper.common.CharHelper.extractEuroPrice;

@RequiredArgsConstructor
@Slf4j
public class NjuskaloScraper {

    private final static String PAGE = "https://www.njuskalo.hr";

    @NonNull
    private final HashRepository repository;
    @NonNull
    private final TelegramClient telegramClient;

    public void scrape(String slug) {
        log.info("Njuskalo scraper started");

        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setCssEnabled(false);
        //ovo iskljucis kad ti ne radi da dobijes link za captchu
        client.getOptions().setJavaScriptEnabled(false);

        String searchUrl = PAGE + slug + "&page=";
        for (int pageNumber = 1; pageNumber <= 3; pageNumber++) {
            HtmlPage htmlPage = null;
            try {
                String url = searchUrl + pageNumber;
                log.info("Checking page number {}\nLink {}", pageNumber, url);
                htmlPage = client.getPage(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Retrieve all <li> elements
            List<HtmlElement> items = htmlPage.getByXPath("//ul[contains(@class, 'EntityList-items')]");
            if (!items.isEmpty()) {
                //2 je ova normalnla lista na prvoj stranici
                //0 je ova normalnla lista na svim ostalim
                int liStart = 0;
                if(pageNumber==1){
                    liStart = 2;
                }
                for (int i = liStart; i < items.size() - 1; i++) {

                    HtmlElement item = items.get(i);

                    Iterator<DomElement> iterator = item.getChildElements().iterator();
                    int count = 25;
                    while (count > 0) {
                        boolean hasNext = iterator.hasNext();
                        if (hasNext) {
                            DomElement next = iterator.next();
                            Item.ItemBuilder itemBuilder = Item.builder();
                            parseLiElement(next, itemBuilder);
                            Item item1 = itemBuilder.build();
                            if (item1.getTitle() != null) {
                                checkIfInRepo(item1, itemBuilder);
                                count--;
                            }
                        }
                    }
                }
            } else {
                log.info("No items found");
            }
        }
        log.info("Njuskalo scraper done");
    }

    private void parseLiElement(DomElement liElement, Item.ItemBuilder itemBuilder) {
        DomElement firstElementChild = liElement.getFirstElementChild();
        firstElementChild.getChildElements().forEach(domElement -> {
            if (domElement.getAttribute("class").equals("entity-title")) {
                String titleContent = domElement.getTextContent();
                itemBuilder.title(cleanWierdLetters(titleContent));
                String link = domElement.getElementsByTagName("a").get(0).getAttribute("href");
                itemBuilder.link(PAGE + link);
            }
            if (domElement.getAttribute("class").equals("entity-pub-date")) {
                String time = domElement.getElementsByTagName("time").get(0).getAttribute("datetime");
                itemBuilder.date(time);
            }
            if (domElement.getAttribute("class").equals("entity-prices")) {
                String price = domElement.getElementsByTagName("strong").get(0).getTextContent();
                price = price.replace(" ", "");
                itemBuilder.price(extractEuroPrice(price));
            }
        });
    }

    private void checkIfInRepo(Item item, Item.ItemBuilder itemBuilder) {
        NjuskaloOglasScraper njuskaloOglasScraper = new NjuskaloOglasScraper();
        String hash = item.hash();
        long count = repository.countByHash(hash);
        if (count == 0) {
            try {
                itemBuilder.pageAd(njuskaloOglasScraper.getPageAdItem(item.getLink()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            item = itemBuilder.build();
            telegramClient.send(item);
            HashRecord hashRecord = new HashRecord(hash, item.getPrice(), item.getLink(), item.getDate());
            repository.save(hashRecord);
           log.info(item.toString());
        }else{
            //log.info("Item already in repo: {}", hash);
        }
    }
}

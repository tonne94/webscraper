package com.test.webscraper.oglasnik;

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
public class OglasnikScraper {

    private final static String PAGE = "https://www.oglasnik.hr/";

    @NonNull
    private final HashRepository repository;
    @NonNull
    private final TelegramClient telegramClient;

    public void scrape(String slug) {
        log.info("oglasnik.hr scraper started");

        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        String searchUrl = PAGE + slug + "&page=";
        for (int pageNumber = 1; pageNumber <= 3; pageNumber++) {
            HtmlPage htmlPage = null;

            try {
                String url = searchUrl + pageNumber;
                htmlPage = client.getPage(url);
                log.info("Checking page number {}\nLink {}", pageNumber, url);
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
            List<HtmlElement> items = htmlPage.getByXPath("//div[contains(@class, 'oglasnik-box content-box')]");
            if(!items.isEmpty()){
                HtmlElement item = items.get(0);
                Iterator<DomElement> iterator = item.getChildElements().iterator();
                int count = 20;
                while (count > 0) {
                    boolean hasNext = iterator.hasNext();
                    if (hasNext) {
                        DomElement next = iterator.next();
                        Item.ItemBuilder itemBuilder = Item.builder();
                        parseListElement(next, itemBuilder);
                        Item item1 = itemBuilder.build();
                        if (item1.getTitle() != null) {
                            checkIfInRepo(item1, itemBuilder);
                            count--;
                        }
                    }
                }
            }else{
                log.info("No items found");
            }
            log.info("oglasnik.hr scraper done");
        }
    }

    private void checkIfInRepo(Item item, Item.ItemBuilder itemBuilder) {
        String hash = item.hash();
        long count = repository.countByHash(hash);
        if (count == 0) {
            telegramClient.send(item);
            HashRecord hashRecord = new HashRecord(hash, item.getPrice(), item.getLink(), item.getDate());
            repository.save(hashRecord);
            log.info(item.toString());
        }
    }

    private void parseListElement(DomElement next, Item.ItemBuilder itemBuilder) {
        String link = next.getAttribute("href");
        itemBuilder.link(link);

        next.getChildElements().forEach(domElement -> {
            if(domElement.getAttribute("class").equals("pad-xs-only-lr")){
                domElement.getChildElements().forEach(domElement1 -> {
                    if(domElement1.getAttribute("class").equals("classified-title")){
                        itemBuilder.title(cleanWierdLetters(domElement1.getTextContent()));
                    }
//                    if(domElement1.getAttribute("class").equals("info-wrapper")){
//                        domElement1.getChildElements().forEach(domElement2 -> {
//                            if(domElement2.getAttribute("class").equals("description")){
//                                domElement2.getChildElements().forEach(domElement3 -> {
//                                    if(domElement3.getAttribute("class").equals("classified-param")){
//                                        if(domElement3.getFirstChild().getTextContent().startsWith("Povr")){
//                                            domElement3.getChildElements().forEach(domElement4 -> {
//                                                String textContent = domElement4.getTextContent();
//                                                da bi izvuako povrsinu ali je u page add
//                                            });
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                    }
                    if(domElement1.getAttribute("class").equals("price-block")){
                        domElement1.getChildElements().forEach(domElement2 -> {
                            if(domElement2.getAttribute("class").equals("price-values")){
                                domElement2.getChildElements().forEach(domElement3 -> {
                                    if(domElement3.getAttribute("class").equals("main")){
                                        String price = domElement3.getTextContent();
                                        itemBuilder.price(extractEuroPrice(price));
                                    }
                                });
                            }
                        });
                    }
                    if(domElement1.getAttribute("class").equals("meta classified-box-end")){
                        domElement1.getChildElements().forEach(domElement2 -> {
                            if(domElement2.getAttribute("class").equals("date")){
                                itemBuilder.date(domElement2.getTextContent());
                            }
                        });
                    }
                });
            }
        });
    }

}

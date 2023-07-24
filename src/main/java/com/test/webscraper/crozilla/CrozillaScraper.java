package com.test.webscraper.crozilla;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.test.webscraper.common.Item;
import com.test.webscraper.njuskalo.NjuskaloOglasScraper;
import com.test.webscraper.repository.HashRecord;
import com.test.webscraper.repository.HashRepository;
import com.test.webscraper.telegram.TelegramClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.test.webscraper.common.CharHelper.cleanWierdLetters;
import static com.test.webscraper.common.CharHelper.extractEuroPrice;

@RequiredArgsConstructor
@Slf4j
public class CrozillaScraper {

    private final static String PAGE = "https://www.crozilla.com/";
    private final static String COOKIE = "currency=eyJpdiI6Ikg3cEMyTElnd0xZVWxCckdQTVdtanc9PSIsInZhbHVlIjoiSFZ1Slk3YjF1ZW1iakMrN2xSU2pHald4WFdoUjFLZGxMTllwSFRCNlB4SnlNb2xDbTJDK2dPTkJ3cEJPdTNjWWdpeG9qaHYzcDVoelpKXC9yRUVKa2d4cCtwM2U2cmFXcVQ3eEo3WWxESlNRPSIsIm1hYyI6ImZlZWNkNDJlYTVkYzM4MDEyOTFjZWE1ZjM0Yzg3NjA5ZjIyN2RkOWY4YTIyZGVjNDc3MmZjZGU5ZTBmZmZkYzkifQ==; _gcl_au=1.1.1582812317.1657646665; _ga=GA1.2.46738724.1657646680; addtl_consent=1~39.4.3.9.6.9.13.6.4.15.9.5.2.7.4.1.7.1.3.2.10.3.5.4.21.4.6.9.7.10.2.9.2.18.7.6.14.5.20.6.5.1.3.1.11.29.4.14.4.5.3.10.6.2.9.6.6.4.5.4.4.29.4.5.3.1.6.2.2.17.1.17.10.9.1.8.6.2.8.3.4.142.4.8.42.15.1.14.3.1.8.10.25.3.7.25.5.18.9.7.41.2.4.18.21.3.4.2.1.6.6.5.2.14.18.7.3.2.2.8.20.8.8.6.3.10.4.20.2.13.4.6.4.11.1.3.22.16.2.6.8.2.4.11.6.5.33.11.8.1.10.28.12.1.3.21.2.7.6.1.9.30.17.4.9.15.8.7.3.6.6.7.2.4.1.7.12.13.22.13.2.12.2.10.5.15.2.4.9.4.5.4.7.13.5.15.4.13.4.14.8.2.15.2.5.5.1.2.2.1.2.14.7.4.8.2.9.10.18.12.13.2.18.1.1.3.1.1.9.25.4.1.19.8.4.5.3.5.4.8.4.2.2.2.14.2.13.4.2.6.9.6.3.4.3.5.2.3.6.10.11.6.3.16.3.11.3.1.2.3.9.19.11.15.3.10.7.6.4.3.4.6.3.3.3.3.1.1.1.6.11.3.1.1.11.6.1.10.5.2.6.3.2.2.4.3.2.2.7.15.7.12.2.1.3.3.4.5.4.3.2.2.4.1.3.1.1.1.2.9.1.6.9.1.5.2.1.7.2.8.11.1.3.1.1.2.1.3.2.6.1.12.5.3.1.3.1.1.2.2.7.7.1.4.1.2.6.1.2.1.1.3.1.1.4.1.1.2.1.8.1.7.4.3.2.1.3.5.3.9.6.1.15.10.28.1.2.2.12.3.4.1.6.3.4.7.1.3.1.1.3.1.5.3.1.3.2.2.1.1.4.2.1.2.1.2.2.2.4.2.1.2.2.2.4.1.1.1.2.2.1.1.1.1.2.1.1.1.2.2.1.1.2.1.2.1.7.1.2.1.1.1.2.1.1.1.1.2.1.1.3.2.1.1.8.1.1.1.5.2.1.6.5.1.1.1.1.1.2.2.3.1.1.4.1.1.2.2.1.1.4.3.1.2.2.1.2.1.2.3.1.1.2.4.1.1.1.5.1.3.6.3.1.5.2.3.4.1.2.3.1.4.2.1.2.2.2.1.1.1.1.1.1.11.1.3.1.1.2.2.5.2.3.3.5.1.1.1.4.2.1.1.2.5.1.9.4.1.1.3.1.7.1.4.5.1.7.2.1.1.1.2.1.1.1.4.2.1.12.1.1.3.1.2.2.3.1.2.1.1.1.2.1.1.2.1.1.1.1.2.1.3.1.5.1.2.4.3.8.2.2.9.7.2.3.2.1.4.6.1.1.6.1.1; euconsent-v2=CPb_z8APb_z8AAKAqAHRCXCsAP_AAH_AAA6II4Nd_X__bX9j-_5_aft0eY1P9_r37uQzDhfNs-8F3L_W_LwXw2E7NF36pq4KmR4Eu3LBIQNlHMHUTUmwaokVrzHsak2cpyNKJ7JEknMZO2dYGF9Pn9lDuYKY7_5_9_bx2D-t_9_-39T378Xf3_dp_2_--vCfV599jfn9fV_789KP___9v-_8__________38EbwCTDVuIAuzLHBk0DCKFECMKwkKoFABBQDC0RWADg4KdlYBLrCFgAgFSEYEQIMQUYMAgAEEgCQiICQAsEAiAIgEAAIAEQCEABEwCCwAsDAIAAQDQsQAoABAkIMiAiOUwICoEgoJbKxBKCvY0wgDrPACgURsVAAiSQEUgICQsHAcASAl4skDTFC-QAjBCgFEAAAA.fngAAAAAAAAA; __qca=P0-1985626529-1657646681097; _oid=08126cd3-79cd-443d-87a9-3f50e0977193; _hjSessionUser_3012226=eyJpZCI6Ijc3NmZlYmMzLTlhY2YtNTFlZi1hMDJlLTcyODUwOWJjYjQ2YSIsImNyZWF0ZWQiOjE2NTc2NDY2ODIxOTUsImV4aXN0aW5nIjp0cnVlfQ==; qcSxc=1661975930540; _gid=GA1.2.1028467545.1664101019; _hjIncludedInSessionSample=0; _hjSession_3012226=eyJpZCI6ImM2ZWY5YmJmLWU3NGEtNDQxOC04YzNjLTQ0MWY2NWM4YTFkYSIsImNyZWF0ZWQiOjE2NjQxMTc1OTgxMjcsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=0; reese84=3:94tpMJAjdPZQp0VVbx8Gyg==:FGWKPy2VBu9xvZqamAZbMx1mqR5bsuDSXBVN8W/iZHbol0JWgJmZbVruaFuXUiFACZkoTyn7Co/9mrCMSnVQHNCvVkNbcf3awk2Y9QVoK0eJkn/253j4TAhpbehi20OgZBMKluIWhuQtoKZpFygc0SgpOQCgG+j04QaSXieSD5w3ATlzjgePI4H4hhzwQy66Mvew9JxCEYHVd4tNjrZBAYwcAvaW/8tCclWGVFpLV3wNHhdrkKVuDYo8Z5qHGXi3+PjL6D+BFL/xr/u/M5tKlqGFj++4dKiQGE9GL5mZfZVnHizkbMYoVmNB/Ra9FdqBOHvFMRtEaPjwGpIvlXa02EBa2cU2s/pQpslBWaF4BPKE+gbIuefQFqLKnrt8YKpWmZ+Dd3Q8Pc/95lFlt54kNVvSME6PQb/OHwbX6tcsqv2G5HhLhVlYDLu6Y6eToVJ5RYvG2tEvjLhMiZAtyTNFCA==:nHviiUgFnJv8wrCB7WQgcZDe+6lv6z0xLdtWz6bHb0Y=; propertySearchPreferences=eyJpdiI6Ik0zQzJtVEFKVWZ3WUtMVVE1b2J1ZXc9PSIsInZhbHVlIjoieTl5cFNtMklCVEJrOGUwRFU1QkRvRTZsY3dJS0o0RWp4XC85aGtRRG1LNXpJNXJaSkRZVGtLOTFPa1VWRHh4b0xXcEtXZ21UMmdxcjNoXC9tWFZaS1I5NkRnRUV0XC81YXRoUkdLdkpqQXNSVmF5N0VqbEhWUWQ4aXJ0RExlRWFISWhGc1wvTU54SzNGU1FmTXEzNVltSFwveFZQRlNWdStld3VOQm5RNWsyMnlYOWlFdlRJaElrejlwQjB4dTAwNTFRczd5XC90M3J0OGM2ZnRoV2RtV25DU3lSZ09Yb21LcDFoMVhxcHJaaDZBazQ0Yz0iLCJtYWMiOiI5OGI2YWRhMmFjOGI4NGNkOGNmZTI0MGFmY2NlMDI5ZDM4ZDg0ZjBiZDhmNDgzZWQ1ZDc0YWQ3MjM3YjBmNWNkIn0=; XSRF-TOKEN=eyJpdiI6IlRSNTUreFFiNzljYzhlY293MjV5TkE9PSIsInZhbHVlIjoiK1J4M2taWnFxTUtOQ25TMnNrN3VDaHNtandOditKS0FOZ0tEZ0ZLTnZ0MFZvRHFMdkJuRnRlcGJPdlJ3QnJBZzl5cUwzXC9XcnZkZjNPWGNGZnJxRU5RPT0iLCJtYWMiOiJmODY3Mjg5YmVhZjM0NDU5NTA4ODIwOTQwZjBhYzgxYWMyYzY3MzI5NTQ5Njk0MWE2YTNhNmZmZGM1MzU3YjJhIn0=; laravel_session=eyJpdiI6ImN2emp0STlNbHZ6Z2NBWlRENm1PWnc9PSIsInZhbHVlIjoidVVtM0ZxRUpnR0VJRDAwNDdcL1hxcE84a1R4NEVNT3ltQXh2Qm5Hd2NLNUVEcmxIQmpEOHZxM2FsbzNBcVY5NVFMejNDMkxhSGRMYXdEeHk2K3pWbUdnPT0iLCJtYWMiOiI5NDllNDVhZjVjODRjZjEyZGZlMTM4NjNkN2U2YzA4MmEwNTZkYWI0N2ZmODYzNmYwNGI0MDU5MWI5OWI4MTgzIn0=; _gat_UA-491886-2=1";

    @NonNull
    private final HashRepository repository;
    @NonNull
    private final TelegramClient telegramClient;

    public void scrape() {
        log.info("Crozilla scraper started");

        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setCssEnabled(false);
        //ovo iskljucis kad ti ne radi da dobijes link za captchu
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setThrowExceptionOnScriptError(false);

        String searchUrl = PAGE + "/na_prodaju-stanovi/zagreb/area-ids_457791,457792,457790/living-area-low_65/price-high_220000";
        HtmlPage htmlPage = null;
        URL url = null;
        try {
            url = new URL(searchUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        client.addCookie(COOKIE, url, null);


        try {
            htmlPage = client.getPage(searchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Retrieve all <li> elements
//        List<HtmlElement> items = htmlPage.getByXPath("//div[contains(@class, 'results-container')]");
        List<HtmlElement> items = htmlPage.getByXPath("//*");
        if (!items.isEmpty()) {
//            //trece je ova normalnla lista
//            for (int i = 2; i < items.size() - 1; i++) {
//
//                HtmlElement item = items.get(i);
//
//                Iterator<DomElement> iterator = item.getChildElements().iterator();
//                int count = 10;
//                while (count > 0) {
//                    boolean hasNext = iterator.hasNext();
//                    if (hasNext) {
//                        DomElement next = iterator.next();
//                        Item.ItemBuilder itemBuilder = Item.builder();
//                        parseLiElement(next, itemBuilder);
//                        Item item1 = itemBuilder.build();
//                        if (item1.getTitle() != null) {
//                            checkIfInRepo(item1, itemBuilder);
//                            count--;
//                        }
//                    }
//                }
//            }
        } else {
            log.info("No items found");
        }
        log.info("Crozilla scraper done");
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
                itemBuilder.pageAd(njuskaloOglasScraper.getPageAdItem(PAGE + item.getLink()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            telegramClient.send(item);
            HashRecord hashRecord = new HashRecord(hash, item.getPrice(), item.getLink(), item.getDate());
            repository.save(hashRecord);
            log.info(item.toString());
        }
    }
}

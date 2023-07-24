package com.test.webscraper.njuskalo;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.test.webscraper.common.PageAd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.test.webscraper.common.CharHelper.cleanWierdLetters;


public class NjuskaloOglasScraper {

    public PageAd getPageAdItem(String searchUrl) throws IOException {

        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        HtmlPage htmlPage = client.getPage(searchUrl);

        PageAd.PageAdBuilder builder = PageAd.builder();
        List<HtmlElement> items = htmlPage.getByXPath("//dl[contains(@class, 'ClassifiedDetailBasicDetails-list cf')]");
        if (!items.isEmpty()) {
            HtmlElement htmlElement = items.get(0);

            List<DomElement> actualList = new ArrayList<>();
            htmlElement.getChildElements().iterator().forEachRemaining(actualList::add);

            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < actualList.size(); i = i + 2) {
                map.put(clean(actualList.get(i).getTextContent()), clean(actualList.get(i + 1).getTextContent()));
            }

            builder.location(map.get("Lokacija"));
            builder.type(map.get("Tipstana"));
            builder.numFloors(map.get("Brojetaza"));
            builder.numRooms(map.get("Brojsoba"));
            builder.floor(map.get("Kat"));
            builder.maxFloor(map.get("Ukupnibrojkatova"));
            String nettopovrsina = map.get("Nettopovrsina");
            String stambenapovrsina = map.get("Stambenapovrsina");
            if(nettopovrsina!=null){
                builder.area(nettopovrsina);
                builder.areaDouble(getNumber(nettopovrsina));
            }
            if(stambenapovrsina!=null){
                builder.area(nettopovrsina);
                builder.areaDouble(getNumber(stambenapovrsina));
            }
            builder.yearBuilt(map.get("Godinaizgradnje"));
            builder.yearRenovated(map.get("Godinazadnjerenovacije"));
            builder.balcony(map.get("Balkon/Lodja/Terasa"));
            builder.energy(map.get("Energetskirazred"));
            builder.id(map.get("Sifraobjekta"));
        }
        PageAd build = builder.build();
        return build;
    }

    private String clean(String input) {
        input = input.replaceAll("\\s+", "");
        return cleanWierdLetters(input);
    }

    private Double getNumber(String input) {
        input = input.replace(",", ".").replaceAll("[^0-9\\.]", "");
        return Double.parseDouble(input);
    }
}

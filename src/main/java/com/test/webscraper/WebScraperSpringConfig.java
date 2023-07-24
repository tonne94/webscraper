package com.test.webscraper;

import com.test.webscraper.common.Item;
import com.test.webscraper.crozilla.CrozillaScraper;
import com.test.webscraper.oglasnik.OglasnikScraper;
import com.test.webscraper.njuskalo.NjuskaloScraper;
import com.test.webscraper.repository.HashRepository;
import com.test.webscraper.telegram.TelegramClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;

@Configuration
public class WebScraperSpringConfig {

    @Autowired
    private HashRepository repository;

    @Bean
    public TelegramClient client(){
        return new TelegramClient(
                new ArrayBlockingQueue<>(10000),
                Executors.newSingleThreadExecutor()
        );
    }

    @Bean
    public NjuskaloScraper njuskaloScraper(TelegramClient client){
        NjuskaloScraper scraper = new NjuskaloScraper(repository, client);
//        scraper.scrape();
        return scraper;
    }

    @Bean
    public OglasnikScraper oglasnikScraper(TelegramClient client){
        OglasnikScraper scraper = new OglasnikScraper(repository, client);
//        scraper.scrape();
        return scraper;
    }

//    @Bean
//    public CrozillaScraper crozillaScraper(TelegramClient client){
//        CrozillaScraper scraper = new CrozillaScraper(repository, client);
//        scraper.scrape();
//        return scraper;
//    }
}

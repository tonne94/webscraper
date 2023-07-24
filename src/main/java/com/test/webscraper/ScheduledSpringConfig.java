package com.test.webscraper;

import com.test.webscraper.oglasnik.OglasnikScraper;
import com.test.webscraper.njuskalo.NjuskaloScraper;
import com.test.webscraper.telegram.TelegramClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@Import(WebScraperSpringConfig.class)
@RequiredArgsConstructor
@Slf4j
@ConfigurationPropertiesScan
public class ScheduledSpringConfig {

    @NonNull
    private final NjuskaloScraper njuskaloScraper;
    @NonNull
    private final OglasnikScraper oglasnikScraper;
    @NonNull
    private final TelegramClient telegramClient;

    @Value("${njuskalo.stan}")
    private String njuskaloStan;

    @Value("${njuskalo.kuca}")
    private String njuskaloKuca;

    @Value("${oglasnik.stan}")
    private String oglasnikStan;

    @Value("${njuskalo.stan.enabled}")
    private boolean njuskaloStanEnabled;

    @Value("${njuskalo.kuca.enabled}")
    private boolean njuskaloKucaEnabled;

    @Value("${oglasnik.stan.enabled}")
    private boolean oglasnikStanEnabled;

//    @Scheduled(cron = "0 */10 * * * ?")
    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void scheduled(){

        log.info("scheduled " + LocalDateTime.now().toString());
        long startTime = System.currentTimeMillis();
        if(njuskaloStanEnabled){
            njuskaloScraper.scrape(njuskaloStan);
        }
        if(njuskaloKucaEnabled){
            njuskaloScraper.scrape(njuskaloKuca);
        }
        if(oglasnikStanEnabled){
            oglasnikScraper.scrape(oglasnikStan);
        }
        long endTime = System.currentTimeMillis();
        log.info("scheduled done " + LocalDateTime.now().toString());
        log.info("took {} seconds", (endTime-startTime)/1000);
    }

//    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
//    public void telegramScheduled(){
//        try {
//            telegramClient.processSend();
//        } catch (InterruptedException e) {
//            log.info("Exception is thrown while trying to send message", e);
//        }
//    }
}

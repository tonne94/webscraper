package com.test.webscraper.telegram;


import com.google.common.util.concurrent.RateLimiter;
import com.test.webscraper.common.Item;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

@RequiredArgsConstructor
@Slf4j
public class TelegramClient {

    @Value("${telegram.enabled}")
    private boolean enabled;

    private final BlockingQueue<Item> queue;

    private final ExecutorService executorService;

    private RateLimiter rateLimiter = RateLimiter.create(1.0);

    public void send(Item item) {
//        queue.add(item);
        log.info("Item added. Queue size is: "+queue.size());
        if (enabled) {
            executorService.submit(new TelegramSendTask(item));
        }
    }

    public void processSend() throws InterruptedException {
        while(!Thread.currentThread().isInterrupted()){
            log.info("Scheduled called trying to send");
            Item item = queue.take();
            if (enabled) {
                executorService.submit(new TelegramSendTask(item));
            }
        }
    }

    @RequiredArgsConstructor
    class TelegramSendTask implements Runnable {
        @NonNull
        private final Item item;
        @Override
        public void run() {
//            rateLimiter.acquire();
            try {
                String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
                //        https://t.me/+TftjSwpQ8RwxMzNk
                //        5723027555:AAEH4FY7uuIs51WnycN79nJ74OLyIMEf6ko
                //Add Telegram token (given Token is fake)
                String apiToken = "5723027555:AAEH4FY7uuIs51WnycN79nJ74OLyIMEf6ko";

                //Add chatId (given chatId is fake)
                String chatId = "-1001706878794";
                String text = URLEncoder.encode(item.toString(), "UTF-8");

                log.info("Sending: "+item.getTitle() +"\n\tLink:"+item.getLink());

                urlString = String.format(urlString, apiToken, chatId, text);

                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();
                InputStream is = new BufferedInputStream(conn.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

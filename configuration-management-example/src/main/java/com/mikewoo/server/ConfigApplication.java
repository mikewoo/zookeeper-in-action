package com.mikewoo.server;

import com.mikewoo.server.client.ConfigClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Eric Gui
 * @date 2018/9/4
 */
public class ConfigApplication {

    public final static String NAMESPACE = "mikewoo";

    public static void main(String[] args) {
        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        for (int i = 1; i <= threadCount; i++) {
            final int id = i;
            executorService.execute(() -> {
                ConfigClient client = new ConfigClient();
                client.start(NAMESPACE, id);
            });
        }

        executorService.shutdown();
    }
}

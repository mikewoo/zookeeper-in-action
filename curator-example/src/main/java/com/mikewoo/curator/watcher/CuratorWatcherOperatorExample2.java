package com.mikewoo.curator.watcher;

import com.mikewoo.curator.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.util.concurrent.CountDownLatch;

/**
 * {@link CuratorFramework} 操作Zookeeper节点，设置Watcher
 *
 * @author Eric Gui
 * @date 2018/9/3
 */
@Slf4j
public class CuratorWatcherOperatorExample2 {

    public static void main(String[] args) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CuratorClient client = new CuratorClient("mikewoo");
        CuratorFrameworkState state = client.getCurator().getState();
        log.info("state: {}", state);

        try {
            String path = "/curator/watcher";

            NodeCache nodeCache = new NodeCache(client.getCurator(), path);

            // 初始化NodeCache, 若buildInitial=true，初始化时会获取node的值并进行缓存。
            //nodeCache.start();
            nodeCache.start(true);

            if (nodeCache.getCurrentData() != null) {
                log.info("node current data: {}.", new String(nodeCache.getCurrentData().getData()));
            } else {
                log.warn("node initial data is empty.");
            }

            nodeCache.getListenable().addListener(() -> {
                if (nodeCache.getCurrentData() != null) {
                    byte[] data = nodeCache.getCurrentData().getData();
                    log.info("node [{}], current data is {}", nodeCache.getCurrentData().getPath(), new String(data));
                } else {
                    log.warn("node notify none.");
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            log.warn("exception happened", e);
        } finally {
            client.close();
            state = client.getCurator().getState();
            log.info("state: {}", state);
        }
    }

}

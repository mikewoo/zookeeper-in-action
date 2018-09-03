package com.mikewoo.curator.watcher;

import com.mikewoo.curator.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * {@link CuratorFramework} 操作Zookeeper节点，设置子节点Watcher
 *
 * @author Eric Gui
 * @date 2018/9/3
 */
@Slf4j
public class CuratorWatcherOperatorExample3 {

    public static void main(String[] args) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CuratorClient client = new CuratorClient("mikewoo");
        CuratorFrameworkState state = client.getCurator().getState();
        log.info("state: {}", state);

        try {
            String path = "/curator";

            // 为子节点添加Watcher,监听节点的增删改，会触发相应的事件
            PathChildrenCache childrenCache = new PathChildrenCache(client.getCurator(), path, true);

            /**
             * StartMode: 初始化方式
             * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
             * NORMAL：异步初始化
             * BUILD_INITIAL_CACHE：同步初始化
             */
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

            List<ChildData> childDataList = childrenCache.getCurrentData();
            for (ChildData childData : childDataList) {
                log.info("child data: {}", new String(childData.getData()));
            }

            childrenCache.getListenable().addListener((curator, event) -> {
                if (PathChildrenCacheEvent.Type.INITIALIZED == event.getType()) {
                    log.info("child node inited success.");
                } else if (PathChildrenCacheEvent.Type.CHILD_ADDED == event.getType()) {
                    log.info("child node [{}] added.", event.getData().getPath());
                    log.info("current data: {}", new String(event.getData().getData()));
                } else if (PathChildrenCacheEvent.Type.CHILD_UPDATED == event.getType()) {
                    log.info("child node [{}] data updated.", event.getData().getPath());
                    log.info("current data: {}", new String(event.getData().getData()));
                } else if (PathChildrenCacheEvent.Type.CHILD_REMOVED == event.getType()) {
                    log.info("child node [{}] removed.", event.getData().getPath());
                } else {
                    log.info("none info.");
                }
            } );
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

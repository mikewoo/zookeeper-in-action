package com.mikewoo.curator.watcher;

import com.mikewoo.curator.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * {@link CuratorFramework} 操作Zookeeper节点，设置Watcher
 *
 * @author Eric Gui
 * @date 2018/9/3
 */
@Slf4j
public class CuratorWatcherOperatorExample1 {

    public static void main(String[] args) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CuratorClient client = new CuratorClient("mikewoo");
        CuratorFrameworkState state = client.getCurator().getState();
        log.info("state: {}", state);

        try {
            String path = "/curator/watcher";

            client.getCurator().create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(path, "watcher-data".getBytes());

            // 当使用usingWatcher的设置Watcher时，监听只会触发一次，监听完毕后就销毁
            byte[] data = client.getCurator().getData().usingWatcher((CuratorWatcher) (event) -> {
                log.info("get zk server watcher notify, event: {}", event);
                countDownLatch.countDown();
            }).forPath(path);

            log.info("node: {}, data: {}", path, new String(data));

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

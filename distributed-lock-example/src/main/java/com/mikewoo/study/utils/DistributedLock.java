package com.mikewoo.study.utils;

import com.mikewoo.study.curator.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.joda.time.LocalDateTime;

import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具类
 *
 * @author Eric Gui
 * @date 2018/9/7
 */
@Slf4j
public class DistributedLock {

    private CuratorClient client;

    private final static String ZK_DELIMITER = "/";

    private final static String ZK_LOCK_NAMESPACE = "zklock-namespace";

    private final static String ZK_LOCK_PARENT_NODE = "mikewoo-locks";

    private final static String ZK_DISTRIBUTED_LOCK_NODE = "distributed_lock";

    // 用于等待其他用户释放锁
    private static CountDownLatch zkLockLatch = new CountDownLatch(1);

    public DistributedLock(CuratorClient client) {
        this.client = client;
    }

    /**
     * 初始化锁
     */
    public void init() {
        client.getCurator().usingNamespace(ZK_LOCK_NAMESPACE);

        try {
            if (client.getCurator().checkExists().forPath(ZK_DELIMITER + ZK_LOCK_PARENT_NODE) == null) {
                client.getCurator().create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(ZK_DELIMITER + ZK_LOCK_PARENT_NODE);

            }

            addWatchToLock(ZK_DELIMITER + ZK_LOCK_PARENT_NODE);
        } catch (Exception e) {
            log.warn("Create ZK_LOCK_PARENT_NODE failed");
        }
    }

    /**
     * 获取分布式锁
     */
    public boolean getDistributedLock() {
        while (true) {
            try {
                client.getCurator().create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(ZK_DELIMITER + ZK_LOCK_PARENT_NODE + ZK_DELIMITER + ZK_DISTRIBUTED_LOCK_NODE);
                log.info("Get distributed lock: {}", ZK_DISTRIBUTED_LOCK_NODE);
                return true; // 锁节点创建成功，则表示获得锁，退出循环
            } catch (Exception e) {
                log.warn("Get distributed lock failed");
                try {
                    if (zkLockLatch.getCount() <= 0) {
                        zkLockLatch = new CountDownLatch(1);
                    }

                    zkLockLatch.await();
                } catch (InterruptedException e1) {
                    log.warn("zkLockLatch await happened exception ", e1);
                }
            }
        }
    }

    /**
     * 获取分布式锁，带超时时间
     */
    public boolean getDistributedLock(int timeout) {
        long begin = System.currentTimeMillis();
        while (true) {
            try {
                client.getCurator().create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(ZK_DELIMITER + ZK_LOCK_PARENT_NODE + ZK_DELIMITER + ZK_DISTRIBUTED_LOCK_NODE);
                log.info("Get distributed lock: {}", ZK_DISTRIBUTED_LOCK_NODE);
                return true; // 锁节点创建成功，则表示获得锁，退出循环
            } catch (Exception e) {
                log.warn("Get distributed lock failed");
                try {
                    if (zkLockLatch.getCount() <= 0) {
                        zkLockLatch = new CountDownLatch(1);
                    }

                    zkLockLatch.await(timeout, TimeUnit.MILLISECONDS);
                    if (System.currentTimeMillis() - begin >= timeout)
                        return false;
                } catch (Exception e1) {
                    log.warn("zkLockLatch await happened exception ", e1);
                    return false;
                }
            }
        }


    }

    /**
     * 释放分布式锁
     */
    public boolean releaseDistributedLock() {
        try {
            String path = ZK_DELIMITER + ZK_LOCK_PARENT_NODE + ZK_DELIMITER + ZK_DISTRIBUTED_LOCK_NODE;
            if (client.getCurator().checkExists().forPath(path) != null) {
                client.getCurator().delete().guaranteed().forPath(path);
            }
        } catch (Exception e) {
            log.warn("Release distributed lock [{}] failed", ZK_DISTRIBUTED_LOCK_NODE);
            return false;
        }
        log.info("Distributed lock [{}] has been released", ZK_DISTRIBUTED_LOCK_NODE);
        return true;
    }

    /**
     * 添加节点Watcher事件
     *
     * @param nodePath
     */
    private void addWatchToLock(String nodePath) throws Exception {
        PathChildrenCache childrenCache = new PathChildrenCache(client.getCurator(), nodePath, true);
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        childrenCache.getListenable().addListener((curatorClient, event) -> {
            if (PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(event.getType())) {
                String path = event.getData().getPath();
                log.info("Distributed lock node [{}] removed, lock has been released.", path);
                if (path.contains(ZK_DISTRIBUTED_LOCK_NODE)) {
                    log.info("The target distributed lock [{}] has been released.", path);
                    zkLockLatch.countDown();
                }
            }
        });
    }


}

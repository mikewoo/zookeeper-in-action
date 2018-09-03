package com.mike.woo.node;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ZK节点操作示例，节点是否存在
 *
 * @author Eric Gui
 * @date 2018/8/31
 */
@Slf4j
public class ZKNodeOperatorExample6 implements Watcher {

    public final static String ZK_STANDALONE_SERVER_PATH = "192.168.33.100:2181";
    public final static String ZK_CLUSTER_SERVER_PATH = "192.168.33.101:2181,192.168.33.102:2181,192.168.33.103:2181";

    public final static Integer TIMEOUT = 5000;

    private static ZooKeeper zooKeeper;

    private static Stat stat = new Stat();

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZKNodeOperatorExample6() {
    }

    public ZKNodeOperatorExample6(String zkServerPath) {
        try {
            zooKeeper = new ZooKeeper(zkServerPath, TIMEOUT, new ZKNodeOperatorExample6());
        } catch (IOException e) {
            log.warn("establish connection happened exception, ", e);
            if (zooKeeper != null) {
                try {
                    zooKeeper.close();
                } catch (InterruptedException e1) {
                    log.warn("close connection happened exception, ", e);
                }
            }
        }
    }

    @Override
    public void process(WatchedEvent event) {
        log.info("收到ZK服务端watch通知:{}", event);
        try {
            if (event.getType() == Event.EventType.NodeDataChanged) {
                log.info("zk node data changed.");
            } else if (event.getType() == Event.EventType.NodeCreated) {
                log.info("create new zk node: {}", event.getPath());
            } else if (event.getType() == Event.EventType.NodeDeleted) {
                log.info("zk node deleted.");
            } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
                log.info("zk node children changed.");
            } else {
                log.info("none info.");
                countDownLatch.countDown();
            }
        } catch (Exception e) {
            log.warn("watcher happened exception", e);
        }
    }

    public static void main(String[] args) throws Exception {
        ZKNodeOperatorExample6 client = new ZKNodeOperatorExample6(ZK_STANDALONE_SERVER_PATH);
        countDownLatch.await();
        log.info("connection state is {}", client.getZooKeeper().getState());

        countDownLatch = new CountDownLatch(1);
        String path = "/mikewoo/book";
        Stat stat = client.getZooKeeper().exists(path, true);
        if (stat != null) {
            log.info("node [{}] exists, data version: {}", path, stat.getVersion());
        } else {
            log.info("node [{}] not exists", path);
        }
        countDownLatch.await();
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }
}

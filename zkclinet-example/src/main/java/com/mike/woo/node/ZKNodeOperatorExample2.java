package com.mike.woo.node;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * ZK节点操作示例，设置节点数据
 * @author Eric Gui
 * @date 2018/8/31
 */
@Slf4j
public class ZKNodeOperatorExample2 implements Watcher {

    public final static String ZK_STANDALONE_SERVER_PATH = "192.168.33.100:2181";
    public final static String ZK_CLUSTER_SERVER_PATH = "192.168.33.101:2181,192.168.33.102:2181,192.168.33.103:2181";

    public final static Integer TIMEOUT = 5000;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private ZooKeeper zookeeper;

    public ZKNodeOperatorExample2() {}

    public ZKNodeOperatorExample2(String zkServerPath) {
        try {
            zookeeper = new ZooKeeper(zkServerPath, TIMEOUT, new ZKNodeOperatorExample2());
        } catch (IOException e) {
            log.warn("establish connection happened exception, ", e);
            if (zookeeper != null) {
                try {
                    zookeeper.close();
                } catch (InterruptedException e1) {
                    log.warn("close connection happened exception, ", e);
                }
            }
        }
    }

    @Override
    public void process(WatchedEvent event) {
        log.info("收到ZK服务端watch通知:{}", event);
        countDownLatch.countDown();
    }

    public static void main(String[] args) throws Exception {
        ZKNodeOperatorExample2 server = new ZKNodeOperatorExample2(ZK_STANDALONE_SERVER_PATH);
        countDownLatch.await();
        log.info("connection state is {}", server.getZookeeper().getState());

        // 设置节点数据，其中version必须更当前version匹配，否则会更新失败，乐观锁机制
        Stat stat = server.getZookeeper().setData("/mikewoo/nodeCreateAsyncTest", "setDataTest".getBytes(), 0);
        log.info("current version: {}", stat.getVersion());

        Stat status = new Stat();
        byte[] data = server.getZookeeper().getData("/mikewoo/nodeCreateAsyncTest", true, status);
        log.info("current data: {}", new String(data));
    }

    public ZooKeeper getZookeeper() {
        return zookeeper;
    }
}

package com.mike.woo.node;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author Eric Gui
 * @date 2018/8/31
 */
@Slf4j
public class ZKNodeOperatorExample1 implements Watcher {

    public final static String ZK_STANDALONE_SERVER_PATH = "192.168.33.100:2181";
    public final static String ZK_CLUSTER_SERVER_PATH = "192.168.33.101:2181,192.168.33.102:2181,192.168.33.103:2181";

    public final static Integer TIMEOUT = 5000;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private ZooKeeper zookeeper;

    public ZKNodeOperatorExample1() {}

    public ZKNodeOperatorExample1(String zkServerPath) {
        try {
            zookeeper = new ZooKeeper(zkServerPath, TIMEOUT, new ZKNodeOperatorExample1());
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

    /**
     * 同步方式创建ZK节点，不支持子节点的递归创建
     * @param path
     * @param data
     * @param acls
     */
    public void createZKNode(String path, byte[] data, List<ACL> acls) {
        try {
            String result = zookeeper.create(path, data, acls, CreateMode.PERSISTENT);
            log.info("create ZKNode: {} success", result);
        } catch (Exception e) {
            log.warn("create ZKNode happened exception, ", e);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        log.info("收到ZK服务端watch通知:{}", event);
        countDownLatch.countDown();
    }

    public static void main(String[] args) throws Exception {
        ZKNodeOperatorExample1 example = new ZKNodeOperatorExample1(ZK_STANDALONE_SERVER_PATH);
        countDownLatch.await();
        example.createZKNode("/mikewoo/nodeCreateTest", "nodeCreateTest-data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE);
    }
}

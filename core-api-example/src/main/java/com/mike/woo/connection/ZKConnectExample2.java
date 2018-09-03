package com.mike.woo.connection;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * {@link ZooKeeper} client 会话连接恢复示例
 * @author Eric Gui
 * @date 2018/8/31
 */
@Slf4j
public class ZKConnectExample2 implements Watcher {

    public final static String ZK_STANDALONE_SERVER_PATH = "192.168.33.100:2181";
    public final static String ZK_CLUSTER_SERVER_PATH = "192.168.33.101:2181,192.168.33.102:2181,192.168.33.103:2181";

    public final static Integer TIMEOUT = 5000;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper(ZK_CLUSTER_SERVER_PATH, TIMEOUT, new ZKConnectExample2());

        long sessionId = zooKeeper.getSessionId();
        byte[] sessionPasswd = zooKeeper.getSessionPasswd();

        log.info("客户端开始连接zookeeper服务器...");
        log.info("当前连接状态： {}", zooKeeper.getState());
        countDownLatch.await();
        log.info("当前连接状态： {}", zooKeeper.getState());

        Thread.sleep(2000);

        // 开始会话重连
        log.info("开始会话重连...");
        countDownLatch = new CountDownLatch(1);

        ZooKeeper zkSession = new ZooKeeper(ZK_CLUSTER_SERVER_PATH,
                                            TIMEOUT,
                                            new ZKConnectExample2(),
                                            sessionId,
                                            sessionPasswd);
        log.info("重新连接状态zkSession：{}", zkSession.getState());
        countDownLatch.await();
        log.info("重新连接状态zkSession：{}", zkSession.getState());

    }

    @Override
    public void process(WatchedEvent event) {
        log.info("收到watch通知： {}", event);
        countDownLatch.countDown();
    }
}

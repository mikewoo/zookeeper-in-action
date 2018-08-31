package com.mike.woo.connection;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * {@link ZooKeeper} clinet 与ZK服务器建立连接示例
 * 客户端和zk服务端链接是一个异步的过程，当连接成功后后，客户端会收的一个watch通知
 * @author Eric Gui
 * @date 2018/8/31
 */
@Slf4j
public class ZKConnectExample1 implements Watcher {

    public final static String ZK_STANDALONE_SERVER_PATH = "192.168.33.100:2181";
    public final static String ZK_CLUSTER_SERVER_PATH = "192.168.33.101:2181,192.168.33.102:2181,192.168.33.103:2181";

    public final static Integer TIMEOUT = 5000;

    private final static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        /**
         * {@link ZooKeeper} client构造函数参数：
         *  @param connectString：要连接的服务器IP字符串，
         * 		比如: "192.168.33.101:2181,192.168.33.102:2181,192.168.33.103:2181"
         * 		其中，如果连接地址是单个IP,则代表ZK单机服务，多个IP则代表ZK集群；另外，在IP后也可以加路径，如192.168.33.100:2181/kafka
         *  @param sessionTimeout：超时时间，心跳收不到了，那就超时
         *  @param watcher：通知事件，如果有对应的事件触发，则会收到一个通知；如果不需要，那就设置为null
         *  @param canBeReadOnly：可读，当这个物理机节点断开后，还是可以读到数据的，只是不能写，
         *                此时数据被读取到的可能是旧数据，此处建议设置为false，不推荐使用
         *  @param sessionId：会话的id
         *  @param sessionPasswd：会话密码	当会话丢失后，可以依据 sessionId 和 sessionPasswd 重新获取会话
         */
        ZooKeeper zooKeeper = new ZooKeeper(ZK_CLUSTER_SERVER_PATH, TIMEOUT, new ZKConnectExample1());

        log.warn("客户端开始连接zookeeper服务器...");
        log.warn("当前连接状态： {}", zooKeeper.getState());
        countDownLatch.await();
        log.warn("当前连接状态： {}", zooKeeper.getState());
    }

    @Override
    public void process(WatchedEvent event) {
        log.warn("收到watch通知： {}", event);
        countDownLatch.countDown();
    }
}

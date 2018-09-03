package com.mikewoo.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

/**
 * Curator 客户端
 * @author Eric Gui
 * @date 2018/9/3
 */
public class CuratorClient {

    public final static String ZK_STANDALONE_SERVER_PATH = "192.168.33.100:2181";
    public final static String ZK_CLUSTER_SERVER_PATH = "192.168.33.101:2181,192.168.33.102:2181,192.168.33.103:2181";

    public final static String DEFAULT_NAMESPACE = "workspace";

    public final static int DEFAULT_TIMEOUT = 10000;

    private CuratorFramework curator = null;

    private String namespace;

    private int timeout;

    private int retryTimes;

    private int sleepMs;

    /**
     * 实例化ZK Client
     */
    public CuratorClient() {
        this(DEFAULT_NAMESPACE);
    }

    public CuratorClient(String namespace) {
        this(namespace, DEFAULT_TIMEOUT);
    }

    public CuratorClient(String namespace, int timeout) {
        this(namespace, timeout, 3, 3000);
    }

    public CuratorClient(String namespace, int timeout, int retryTimes, int sleepMs) {
        this.namespace = namespace;
        this.timeout = timeout;
        this.retryTimes = retryTimes;
        this.sleepMs = sleepMs;
        connect();
    }

    private void connect() {
        // 连接失败重试策略
        RetryPolicy retryPolicy = new RetryNTimes(retryTimes, sleepMs);

        curator = CuratorFrameworkFactory.builder().connectString(ZK_STANDALONE_SERVER_PATH)
                .sessionTimeoutMs(timeout)
                .retryPolicy(retryPolicy)
                .namespace(namespace)
                .build();

        curator.start();
    }

    /**
     * 关闭ZK客户端连接
     */
    public void close() {
        if (curator != null) {
            curator.close();
        }
    }

    public CuratorFramework getCurator() {
        return curator;
    }
}
package com.mikewoo.acl;

import com.mikewoo.utils.AclUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ZK节点ACL示例，ACL: custom digest，自定义用户认证访问-addAuth
 *
 * @author Eric Gui
 * @date 2018/8/31
 */
@Slf4j
public class ZKNodeACLExample3 implements Watcher {

    public final static String ZK_STANDALONE_SERVER_PATH = "192.168.33.100:2181";
    public final static String ZK_CLUSTER_SERVER_PATH = "192.168.33.101:2181,192.168.33.102:2181,192.168.33.103:2181";

    public final static Integer TIMEOUT = 5000;

    private static ZooKeeper zooKeeper;

    private static Stat stat = new Stat();

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZKNodeACLExample3() {
    }

    public ZKNodeACLExample3(String zkServerPath) {
        try {
            zooKeeper = new ZooKeeper(zkServerPath, TIMEOUT, new ZKNodeACLExample3());
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

    /**
     * 同步方式创建ZK节点，不支持子节点的递归创建
     * @param nodePath 创建的路径
     * @param data 节点数据的byte[]
     * @param acls 控制权限策略
     */
    public void createZKNode(String nodePath, byte[] data, List<ACL> acls) {
        try {
            /**
             * createMode：节点类型，枚举类型
             *      PERSISTENT：持久节点
             *      PERSISTENT_SEQUENTIAL：持久顺序节点
             *      EPHEMERAL：临时节点
             *      EPHEMERAL_SEQUENTIAL：临时顺序节点
             */
            String result = zooKeeper.create(nodePath, data, acls, CreateMode.PERSISTENT);
            log.info("create ZKNode: {} success", result);
        } catch (Exception e) {
            log.warn("create ZKNode happened exception, ", e);
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
        ZKNodeACLExample3 client = new ZKNodeACLExample3(ZK_STANDALONE_SERVER_PATH);
        countDownLatch.await();
        log.info("connection state is {}", client.getZooKeeper().getState());

        // acl: custom digest
//        List<ACL> acls = new ArrayList<>();
//        Id phantomId = new Id("digest", AclUtils.getDigestUserPwd("phantom:123456"));
//        Id adminId = new Id("digest", AclUtils.getDigestUserPwd("admin:123456"));
//        acls.add(new ACL(ZooDefs.Perms.ALL, adminId));
//        acls.add(new ACL(ZooDefs.Perms.READ, phantomId));
//        acls.add(new ACL(ZooDefs.Perms.DELETE | ZooDefs.Perms.CREATE, phantomId));
//        client.createZKNode("/aclTest/testDigest", "testDigest".getBytes(), acls);

        // 添加认证
        client.getZooKeeper().addAuthInfo("digest", "phantom:123456".getBytes());

        // 1 创建子节点
        // client.createZKNode("/aclTest/testDigest/createTest", "createTest".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE);

        // 2 获取节点数据
//        Stat stat = new Stat();
//        byte[] data = client.getZooKeeper().getData("/aclTest/testDigest", false, stat);
//        if (stat != null) {
//            log.info("data: {}, version: {}", new String(data), stat.getVersion());
//        }

        // 3 设置节点数据 phantomId -> NoAuth for /aclTest/testDigest
        client.getZooKeeper().setData("/aclTest/testDigest", "haha".getBytes(), 1);
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }
}

package com.mikewoo.curator.acl;

import com.mikewoo.curator.CuratorClient;
import com.mikewoo.curator.utils.AclUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * {@link CuratorFramework} 操作Zookeeper节点，设置acl
 * @author Eric Gui
 * @date 2018/9/4
 */
@Slf4j
public class CuratorACLOperatorExample5 {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CuratorClient client = new CuratorClient("mikewoo");
        CuratorFrameworkState state = client.getCurator().getState();
        log.info("state: {}", state);

        try {
            String path = "/curator/acl/ipAcl";

            List<ACL> acls = new ArrayList<>();
            Id clientId = new Id("ip", "192.168.10.50");
            Id localId = new Id("ip", "192.168.33.100");
            acls.add(new ACL(ZooDefs.Perms.ALL, localId));
            acls.add(new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.CREATE, clientId));

            // 创建节点
            // createNode(client, path, acls);

            // 获取节点数据
            getNodeData(client, path);

            countDownLatch.await();
        } catch (Exception e) {
            log.warn("exception happened", e);
        } finally {
            client.close();
            state = client.getCurator().getState();
            log.info("state: {}", state);
        }
    }

    /**
     * 创建节点
     * @param client
     * @param path
     * @param acls
     * @throws Exception
     */
    private static void createNode(CuratorClient client, String path, List<ACL> acls) throws Exception {
        client.getCurator().create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(acls)
                .forPath(path, "ipAcl".getBytes());
    }

    /**
     * 获取节点数据
     * @param client
     * @param path
     * @throws Exception
     */
    private static void getNodeData(CuratorClient client, String path) throws Exception {
        Stat stat = new Stat();
        byte[] data = client.getCurator().getData().storingStatIn(stat).forPath(path);
        if (stat != null) {
            log.info("node: {}, data: {}", path, new String(data));
            log.info("data version: {}", stat.getVersion());
        } else {
            log.warn("none info.");
        }
    }
}

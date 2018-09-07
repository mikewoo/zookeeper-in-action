package com.mikewoo.study.curator.acl;

import com.mikewoo.study.curator.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * {@link CuratorFramework} 操作Zookeeper节点，设置acl
 * @author Eric Gui
 * @date 2018/9/4
 */
@Slf4j
public class CuratorACLOperatorExample4 {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        // 建立ZK Client连接，指定认证方式
        CuratorClient client = new CuratorClient("mikewoo", "digest", "eric:123456".getBytes());
        CuratorFrameworkState state = client.getCurator().getState();
        log.info("state: {}", state);

        try {
            String path = "/curator/acl/digest2/applyToParents2";

            // 获取节点数据
            // getNodeData(client, path);

            // 更新节点数据
            //setNodeData(client, path);

            // 删除节点
            deleteNode(client, path);

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
     * 删除节点
     * @param client
     * @param path
     * @throws Exception
     */
    private static void deleteNode(CuratorClient client, String path) throws Exception {
        client.getCurator().delete()
                .guaranteed()
                .deletingChildrenIfNeeded()
                .withVersion(1)
                .forPath(path);
    }

    /**
     * 更新节点数据
     * @param client
     * @param path
     * @throws Exception
     */
    private static void setNodeData(CuratorClient client, String path) throws Exception {
        client.getCurator().setData().withVersion(0).forPath(path, "applyToParents2-new".getBytes());
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

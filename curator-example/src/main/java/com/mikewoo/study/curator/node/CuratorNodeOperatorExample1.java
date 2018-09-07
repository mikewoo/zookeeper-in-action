package com.mikewoo.study.curator.node;

import com.mikewoo.study.curator.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

/**
 * {@link CuratorFramework} 操作Zookeeper节点（创建，设置节点数据，查询节点数据，删除节点）
 *
 * @author Eric Gui
 * @date 2018/9/3
 */
@Slf4j
public class CuratorNodeOperatorExample1 {

    public static void main(String[] args) throws Exception {
        CuratorClient client = new CuratorClient("mikewoo");
        CuratorFrameworkState state = client.getCurator().getState();
        log.info("state: {}", state);

        try {
            String path = "/curator/createNodeTest";

            // 创建节点
            // createNode(client, path, "createNodeTest".getBytes());

            // 读取节点数据
            // getNodeData(client, path);

            // 更新节点数据
            //setNodeData(client, path, 0, "createNodeTestNewData".getBytes());

            // 读取节点数据
            //getNodeData(client, path);

            // 删除节点
            deleteNode(client, path, 1);

            Thread.sleep(3000);
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
     * @param version
     * @throws Exception
     */
    private static void deleteNode(CuratorClient client, String path, int version) throws Exception {
        client.getCurator().delete()
                .guaranteed()
                .deletingChildrenIfNeeded()
                .withVersion(version)
                .forPath(path);
    }

    /**
     * 更新节点数据
     *
     * @param client
     * @param path
     * @param data
     * @throws Exception
     */
    private static void setNodeData(CuratorClient client, String path, int version, byte[] data) throws Exception {
        client.getCurator().setData().withVersion(version).forPath(path, data);
    }

    /**
     * 读取节点数据
     *
     * @param client
     * @param path
     * @throws Exception
     */
    private static void getNodeData(CuratorClient client, String path) throws Exception {
        Stat stat = new Stat();
        byte[] data = client.getCurator().getData().storingStatIn(stat).forPath(path);
        log.info("node: {}, data: {}", path, new String(data));
        log.info("data version: {}", stat.getVersion());
    }

    /**
     * 创建节点
     *
     * @param client
     * @param path
     * @throws Exception
     */
    private static void createNode(CuratorClient client, String path, byte[] data) throws Exception {
        client.getCurator().create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(path, data);
    }
}

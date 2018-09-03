package com.mikewoo.curator.node;

import com.mikewoo.curator.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.data.Stat;
import java.util.List;

/**
 * {@link CuratorFramework} 操作Zookeeper节点(获取子节点列表，检查节点是否存在)
 * @author Eric Gui
 * @date 2018/9/3
 */
@Slf4j
public class CuratorNodeOperatorExample2 {

    public static void main(String[] args) {
        CuratorClient client = new CuratorClient("mikewoo");
        CuratorFrameworkState state = client.getCurator().getState();
        log.info("state: {}", state);

        try {
            // 查询子节点
            // String path = "/";
            //getChildNodes(client, path);

            // 判断节点是否存在
            String path = "/curator";
            checkNodeExist(client, path);
        } catch (Exception e) {
            log.warn("exception happened", e);
        } finally {
            client.close();
            state = client.getCurator().getState();
            log.info("state: {}", state);
        }

    }

    /**
     * 判断节点是否存在
     * @param client
     * @param path
     * @throws Exception
     */
    private static void checkNodeExist(CuratorClient client, String path) throws Exception {
        Stat stat = client.getCurator().checkExists().forPath(path);
        if (stat != null) {
            log.info("node [{}] exist, data version: {}", path, stat.getVersion());
        } else {
            log.warn("node [{}] not exist", path);
        }
    }

    /**
     * 查询子节点
     * @param client
     * @param path
     * @throws Exception
     */
    private static void getChildNodes(CuratorClient client, String path) throws Exception {
        List<String> children = client.getCurator().getChildren().forPath(path);
        for (String child : children) {
            log.info("child: {}", child);
        }
    }
}

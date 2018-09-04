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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * {@link CuratorFramework} 操作Zookeeper节点，设置acl
 * @author Eric Gui
 * @date 2018/9/4
 */
@Slf4j
public class CuratorACLOperatorExample2 {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CuratorClient client = new CuratorClient("mikewoo");
        CuratorFrameworkState state = client.getCurator().getState();
        log.info("state: {}", state);

        try {
            String path = "/curator/acl/digest/applyToParents";

            List<ACL> acls = new ArrayList<>();
            Id adminId = new Id("digest", AclUtils.getDigestUserPwd("admin:123456"));
            Id ericId = new Id("digest", AclUtils.getDigestUserPwd("eric:123456"));
            acls.add(new ACL(ZooDefs.Perms.ALL, adminId));
            acls.add(new ACL(ZooDefs.Perms.READ, ericId));
            acls.add(new ACL(ZooDefs.Perms.CREATE | ZooDefs.Perms.DELETE, ericId));

            // 创建节点
            // 在applyToParents设置为true时，由于改变了父节点的权限，这时创建子节点可能就会报KeeperErrorCode = NoAuth异常
            client.getCurator().create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .withACL(acls, true)
                    .forPath(path, "digestAcl".getBytes());

            countDownLatch.await();
        } catch (Exception e) {
            log.warn("exception happened", e);
        } finally {
            client.close();
            state = client.getCurator().getState();
            log.info("state: {}", state);
        }
    }
}

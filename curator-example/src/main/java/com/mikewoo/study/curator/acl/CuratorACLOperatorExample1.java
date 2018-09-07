package com.mikewoo.study.curator.acl;

import com.mikewoo.study.curator.CuratorClient;
import com.mikewoo.study.curator.utils.AclUtils;
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
public class CuratorACLOperatorExample1 {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CuratorClient client = new CuratorClient("mikewoo");
        CuratorFrameworkState state = client.getCurator().getState();
        log.info("state: {}", state);

        try {
            String path = "/curator/acl/digestAcl";

            List<ACL> acls = new ArrayList<>();
            Id adminId = new Id("digest", AclUtils.getDigestUserPwd("admin:123456"));
            Id ericId = new Id("digest", AclUtils.getDigestUserPwd("eric:123456"));
            acls.add(new ACL(ZooDefs.Perms.ALL, adminId));
            acls.add(new ACL(ZooDefs.Perms.READ, ericId));
            acls.add(new ACL(ZooDefs.Perms.CREATE | ZooDefs.Perms.DELETE, ericId));

            // 创建节点
            // withACL第二个参数是applyToParents，设置为true，则将为父节点设置相同ACL,false则不改变父节点ACL。
            // 默认applyToParents可省略，等效于将applyToParents设置为false， 默认父节点的权限为'world,'anyone : cdrwa
            client.getCurator().create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .withACL(acls)
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

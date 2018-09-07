package com.mikewoo.study.lockTest;

import com.mikewoo.study.curator.CuratorClient;
import com.mikewoo.study.utils.DistributedLock;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Eric Gui
 * @date 2018/9/7
 */
@Slf4j
public class DistributedLockTestExample2 {

    public static void main(String[] args) {
        CuratorClient curatorClient = new CuratorClient();
        DistributedLock lock = new DistributedLock(curatorClient);
        lock.init();

        log.info("获取分布式锁...");
        if (lock.getDistributedLock()) {
            log.info("获取分布式锁成功");
            try {
                log.info("执行业务逻辑");
                Thread.sleep(10000);
                // 业务逻辑中，如果某些条件不满足（如下单时库存不足，无法完成下单），也必须释放锁
                //throw new InterruptedException();
            } catch (Exception e) {
                e.printStackTrace();
                log.info("出现异常，释放锁");
                lock.releaseDistributedLock();
                return;
            }
            log.info("执行结束，释放锁");
            lock.releaseDistributedLock();
        } else {
            log.info("获取分布式锁失败");
        }

    }
}

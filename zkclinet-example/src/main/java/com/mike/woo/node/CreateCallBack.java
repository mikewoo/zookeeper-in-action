package com.mike.woo.node;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.AsyncCallback;

/**
 * @author Eric Gui
 * @date 2018/9/1
 */
@Slf4j
public class CreateCallBack implements AsyncCallback.StringCallback {

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
      log.info("create node: {} success", path);
      log.info("ctx: {}", ctx);
    }
}

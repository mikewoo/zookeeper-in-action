package com.mikewoo.utils;

import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

/**
 * @author Eric Gui
 * @date 2018/9/3
 */
public class AclUtils {

    public static String getDigestUserPwd(String id) throws Exception {
        return DigestAuthenticationProvider.generateDigest(id);
    }
}

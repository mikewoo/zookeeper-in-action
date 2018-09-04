package com.mikewoo.server.client;

import com.mikewoo.server.configuration.ServerConfig;
import com.mikewoo.server.curator.CuratorClient;
import com.mikewoo.server.domain.SettingConfig;
import com.mikewoo.server.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.util.concurrent.CountDownLatch;

/**
 * @author Eric Gui
 * @date 2018/9/4
 */
@Slf4j
public class ConfigClient {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void start(String namespace, final long id) {
        CuratorClient curatorClient = new CuratorClient(namespace);

        CuratorFrameworkState state = curatorClient.getCurator().getState();
        while (state == CuratorFrameworkState.STARTED) {
            log.info("[{}] configuration management client started.", id);
            break;
        }

        try {
            PathChildrenCache childrenCache = new PathChildrenCache(curatorClient.getCurator(), ServerConfig.CONFIG_NODE_PATH, true);
            childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

            childrenCache.getListenable().addListener((client, event) -> {
                // 监听节点数据变化
                if (PathChildrenCacheEvent.Type.CHILD_UPDATED == event.getType()) {
                    String configNodePath = event.getData().getPath();
                    if ((ServerConfig.CONFIG_NODE_PATH + ServerConfig.CONFIG_SUB_NODE_PATH).equals(configNodePath)) {
                        log.info("[{}] node [{}] config data has changed.", id, configNodePath);

                        String jsonData = null;
                        if (event.getData().getData() != null) {
                            jsonData = new String(event.getData().getData());
                            log.info("[{}] config data: {}", id, jsonData);
                        } else {
                            log.warn("[{}] node [{}] data is empty.", id, configNodePath);
                            return;
                        }

                        // json数据换换
                        SettingConfig config = null;
                        if (StringUtils.isNotBlank(jsonData)) {
                            config = JsonUtil.json2Bean(jsonData, SettingConfig.class);
                        }

                        // ZK中配置数据解析
                        if (config != null) {
                            int type = config.getType();
                            String ops = config.getOps();
                            String url = config.getUrl();
                            String remark = config.getRemark();

                            // 根据type分类处理
                            switch (type) {
                                case 1: // MySQL DataSource 配置文件管理
                                    if (SettingConfig.ADD_OPS.equals(ops)) {
                                        log.info("[{}] 新增MySQL DataSource配置", id);
                                        log.info("[{}] 配置文件下载路径为： {}", id, url);
                                    } else if (SettingConfig.UPDATE_OPS.equals(ops)) {
                                        log.info("[{}] 更新MySQL DataSource配置", id);
                                        log.info("[{}] 配置文件下载路径为： {}", id, url);
                                    } else if (SettingConfig.DELETE_OPS.equals(ops)) {
                                        log.info("[{}] 删除MySQL DataSource配置", id);
                                    } else {
                                        log.warn("[{}] ops param is invalid", id);
                                    }
                                    break;
                                case 2: // Redis 配置文件管理
                                    if (SettingConfig.ADD_OPS.equals(ops)) {
                                        log.info("[{}] 新增Redis配置", id);
                                        log.info("[{}] 配置文件下载路径为： {}", id, url);
                                    } else if (SettingConfig.UPDATE_OPS.equals(ops)) {
                                        log.info("[{}] 更新Redis配置");
                                        log.info("[{}] 配置文件下载路径为： {}", id, url);
                                    } else if (SettingConfig.DELETE_OPS.equals(ops)) {
                                        log.info("[{}] 删除Redis配置", id);
                                    } else {
                                        log.warn("[{}] ops param is invalid", id);
                                    }
                                    break;
                                case 3: // Kafka 配置文件管理
                                    if (SettingConfig.ADD_OPS.equals(ops)) {
                                        log.info("[{}] 新增Kafka配置", id);
                                        log.info("[{}] 配置文件下载路径为： {}", id, url);
                                    } else if (SettingConfig.UPDATE_OPS.equals(ops)) {
                                        log.info("[{}] 更新Kafka配置");
                                        log.info("[{}] 配置文件下载路径为： {}", id, url);
                                    } else if (SettingConfig.DELETE_OPS.equals(ops)) {
                                        log.info("[{}] 删除Kafka配置", id);
                                    } else {
                                        log.warn("[{}] ops param is invalid", id);
                                    }
                                    break;
                                default: // 无操作
                                    break;

                            }
                        }
                    }
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            log.warn("exception happened", e);
        } finally {
            curatorClient.close();
            state = curatorClient.getCurator().getState();
            log.info("state: {}", state);
        }
    }
}

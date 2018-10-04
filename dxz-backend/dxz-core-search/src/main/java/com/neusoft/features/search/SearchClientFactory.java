package com.neusoft.features.search;

import com.neusoft.features.exception.CustomRuntimeException;
import com.google.common.net.HostAndPort;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SearchClientFactory
 *
 * @author <A>zhang.zhj@neusoft.com</A>
 */
public class SearchClientFactory {

    // cluster name
    private static final String ES_SETTING_CLUSTER_NAME = "cluster.name";
    // 是否只作为客户端，即不存储索引数据，默认值为false
//    private static final String ES_SETTING_NODE_CLIENT = "node.client";
//    // 是否持有索引数据，默认值为true
//    private static final String ES_SETTING_NODE_DATA = "node.data";
//    // 是否为本地节点，本地节点是指在JVM级别中的同级 默认值true
//    private static final String ES_SETTING_NODE_LOCAL = "node.local";

    // ES Client
    private TransportClient client;

    // ES client 设置
    private Map<String, String> settings;

    // ES服务器节点
    private Set<HostAndPort> nodes;

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public Set<HostAndPort> getNodes() {
        return nodes;
    }

    public void setNodes(Set<HostAndPort> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return client
     */
    public TransportClient getClient() throws Exception {

        if (client == null) {
            if (settings == null || settings.isEmpty()) {
                settings = new HashMap<String, String>();
                settings.put(ES_SETTING_CLUSTER_NAME, "elasticsearch");
//                settings.put(ES_SETTING_NODE_CLIENT, "false");
//                settings.put(ES_SETTING_NODE_DATA, "true");
//                settings.put(ES_SETTING_NODE_LOCAL, "true");
            }

            Settings settings = Settings.settingsBuilder().put("cluster.name", "myClusterName").build();
            client = TransportClient.builder().settings(settings).build();

            if (nodes == null || nodes.isEmpty()) {
                throw new CustomRuntimeException("ES服务节点为空");
            }

            // 添加节点
            for (HostAndPort hostAndPort : nodes) {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostAndPort.getHostText()), hostAndPort.getPort()));
            }
        }
        return client;
    }

    public void close() throws Exception {
        if (client != null) {
            client.close();
            client = null;
        }
    }


}
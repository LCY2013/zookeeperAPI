package com.lcydream.open.project;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * Created by luochunyun on 2017/11/4.
 * @author lcy
 */
public class DistributedServer {

    private static final String connectString = "192.168.21.130:2181,192.168.21.131:2181,192.168.21.132:2181";
    private static final int sessionTimeOut = 2000;
    private static final String parentNode = "/servers";

    private ZooKeeper zk = null;

    /**
     * 创建到zk的客户端连接
     * @throws Exception
     */
    public void getConnect() throws Exception{
        zk = new ZooKeeper(connectString, sessionTimeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                //收到事件通知后的回调函数(应该是我们自己的事件处理逻辑)
                System.out.println(watchedEvent.getType() + "---" + watchedEvent.getPath());
                try {
                    zk.getChildren("/",true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 向zk集群注册服务器信息
     * @param hostName
     * @throws Exception
     */
    public void registerServer(String hostName) throws Exception{
        String create = null;
        if(!testExist(parentNode)) {
            zk.create(parentNode, hostName.getBytes()
                    , ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        create = zk.create(parentNode + "/server", hostName.getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostName+"is online.."+create);
    }

    /**
     * 判断节点是否存在
     * @param node
     * @return
     * @throws Exception
     */
    public boolean testExist(String node) throws Exception{
        Stat stat = zk.exists(node,false);
        return stat==null?false:true;
    }

    /**
     * 业务功能
     * @param hostName
     * @throws Exception
     */
    public void handleBusiness(String hostName) throws Exception{
        System.out.println(hostName + "start is working");
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws Exception{
        //获取zk连接
        DistributedServer server =
                new DistributedServer();
        server.getConnect();

        //利用zk连接注册服务器信息
        server.registerServer(args[0]);

        //启动业务功能
        server.handleBusiness(args[0]);

    }

}

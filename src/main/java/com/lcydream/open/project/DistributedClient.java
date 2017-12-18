package com.lcydream.open.project;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luochunyun on 2017/11/4.
 * @author lcy
 */
public class DistributedClient {

    private static final String connectString = "192.168.21.130:2181,192.168.21.131:2181,192.168.21.132:2181";
    private static final int sessionTimeOut = 2000;
    private static final String parentNode = "/servers";
    //注意:加volatile的意义
    private volatile List<String> serverList;
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
                    //重新更新服务器列表，重新注册监听
                    getServerlist();
                    zk.getChildren(parentNode,true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取服务器列表
     * @throws Exception
     */
    public void getServerlist() throws Exception{
        //获取服务器子节点信息，并且对父节点进行监听
        List<String> children = zk.getChildren(parentNode,true);

        //先创建一个局部的list来存服务器信息
        List<String> servers = new ArrayList<String>();
        for (String child : children){
            //child只是子节点的名字
            byte[] data = zk.getData(parentNode+"/"+child,false,new Stat());
            servers.add(new String(data));
        }
        //一次性修改服务器信息，提供给业务线程使用
        serverList = servers;
        System.out.println("服务器列表:"+serverList);
    }

    /**
     * 业务功能
     * @throws Exception
     */
    public void handleBusiness() throws Exception{
        System.out.println("client start is working");
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws Exception{

        //获取zk连接
        DistributedClient client = new DistributedClient();
        client.getConnect();

        //获取servers的子节点信息(并监听)，从中获取服务器信息列表
        client.getServerlist();

        //调用业务方法
        client.handleBusiness();
    }

}

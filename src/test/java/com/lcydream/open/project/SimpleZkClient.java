package com.lcydream.open.project;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 *
 * Created by luochunyun on 2017/10/31.
 */
public class SimpleZkClient {

    private static final String connectString = "192.168.21.130:2181,192.168.21.131:2181,192.168.21.132:2181";
    private static final int sessionTimeOut = 2000;

    ZooKeeper zkClient = null;


    @Before
    public void init() throws Exception{
        zkClient = new ZooKeeper(connectString, sessionTimeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                //收到事件通知后的回调函数(应该是我们自己的事件处理逻辑)
                System.out.println(watchedEvent.getType() + "---" + watchedEvent.getPath());
                try {
                    zkClient.getChildren("/",true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取根节点下的所有节点
     * @throws Exception
     */
    @Test
    public void getChildren() throws Exception{
        List<String> listChild = zkClient.getChildren("/",true);
        for(String child : listChild){
            System.out.println(child);
        }
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 数据的增删改查
     * @throws KeeperException
     * @throws InterruptedException
     */
    //创建数据节点到zk中去
    @Test
    public void testCreate() throws KeeperException,InterruptedException{
        //参数1;要创建的节点的路径 参数2:节点数据 参数3：节点的权限 参数4：节点的类型
        String nodeCreated = zkClient.create("/test2",
                "hello idea".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
        //上传的数据可以是任何类型，但是都要转成byte[]
    }

    //判断idea节点是否存在
    @Test
    public void testExist() throws Exception{
        Stat stat = zkClient.exists("/idea",false);
        System.out.println(stat==null?"not exist":"exist");
    }

    /**
     * 获取znode的数据
     */
    @Test
    public void getDate() throws Exception{
        byte[] data = zkClient.getData("/idea",false,new Stat());

        System.out.println(new String(data));
    }

    /**
     * 删除znode
     */
    @Test
    public void deletezNode() throws Exception{
        //参数2:指定要删除的版本，-1表示删除所有的版本
        zkClient.delete("/idea",-1);
    }

    /**
     * 修改znode
     */
    @Test
    public void setData() throws Exception{
        //参数2:指定要删除的版本，-1表示删除所有的版本
        zkClient.setData("/test","yuanyirong".getBytes(),-1);
        byte[] data = zkClient.getData("/test",false,new Stat());
        System.out.println(new String(data));
    }

    @Test
    public void test(){
        Integer i = 1;
        System.out.print(i.toString());
    }
}






















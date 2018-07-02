package com.princeli.demo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @program: zookeeper-demo
 * @description: ${description}
 * @author: ly
 * @create: 2018-06-28 08:50
 **/
public class ConnectionDemo {
    public static void main(String[] args) {
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            ZooKeeper zooKeeper = new ZooKeeper("192.168.245.128:2181,192.168.245.129:2181,192.168.245.130:2181", 4000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (Event.KeeperState.SyncConnected == event.getState()){
                        countDownLatch.countDown();
                    }

                }
            });
            countDownLatch.await();
            System.out.println(zooKeeper.getState());

            zooKeeper.create("/zk-persis-mic","0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            Thread.sleep(1000);
            Stat stat = new Stat();

            //得到当前节点值
            byte[] bytes = zooKeeper.getData("/zk-persis-mic",null,stat);
            System.out.println(new String(bytes));

            //修改节点值
            zooKeeper.setData("/zk-persis-mic","1".getBytes(),stat.getVersion());

            //得到当前节点值
            byte[] bytes1 = zooKeeper.getData("/zk-persis-mic",null,stat);
            System.out.println(new String(bytes1));

            //删除当前节点
            zooKeeper.delete("/zk-persis-mic",stat.getVersion());

            zooKeeper.close();

            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }


    }
}

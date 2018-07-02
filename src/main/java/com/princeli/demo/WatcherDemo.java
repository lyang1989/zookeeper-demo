package com.princeli.demo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @program: zookeeper-demo
 * @description: ${description}
 * @author: ly
 * @create: 2018-06-28 14:09
 **/
public class WatcherDemo {
    public static void main(String[] args) {
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            ZooKeeper zooKeeper = new ZooKeeper("192.168.245.128:2181,192.168.245.129:2181,192.168.245.130:2181", 4000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.out.println("默认事件:"+event.getType());
                    if (Event.KeeperState.SyncConnected == event.getState()){
                        countDownLatch.countDown();
                    }

                }
            });
            countDownLatch.await();

            zooKeeper.create("/zk-persis-mic","0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);

            Stat stat = zooKeeper.exists("/zk-persis-mic", new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    System.out.println(watchedEvent.getType()+"-->"+watchedEvent.getPath());

                    try {
                        zooKeeper.exists(watchedEvent.getPath(),true);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            stat = zooKeeper.setData("/zk-persis-mic","2".getBytes(),stat.getVersion());

            Thread.sleep(1000);

            zooKeeper.delete("/zk-persis-mic",stat.getVersion());

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

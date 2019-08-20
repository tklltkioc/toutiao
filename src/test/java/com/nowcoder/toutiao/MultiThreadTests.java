package com.nowcoder.toutiao;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class MyThread extends Thread{
    private int pid;
    public MyThread(int pid){
        this.pid=pid;
    }
    public void run(){
        try{
            for (int i = 0; i < 10; i++) {
                Thread.sleep (1000);
                System.out.println (String.format ("%d:%d",pid,i));
            }
        }catch (Exception e){
            e.printStackTrace ();
        }
    }
}

class Consumer implements Runnable{
    private BlockingQueue<String>q;
    public Consumer(BlockingQueue<String>q){this.q=q;}
    @Override
    public void run() {
        try{
            while (true){
                System.out.println(Thread.currentThread().getName()+":"+q.take());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
class Producer implements Runnable{
    private BlockingQueue<String>q;
    public Producer(BlockingQueue<String>q){this.q=q;}
    @Override
    public void run() {
        try{
            for (int i = 0; i < 100; i++) {
             Thread.sleep(1000);
             q.put(String.valueOf(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

public class MultiThreadTests {

    public static void testThread(){
//        for (int i = 0; i < 12; i++) {
//            new MyThread (i).start ();
//        }
        for (int i = 0; i < 10; i++) {
            final int file=i;
            new Thread (new Runnable () {
                @Override
                public void run() {
                    try{
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep (1000);
                            System.out.println (String.format ("T2 %d: %d:",file,i));
                        }
                    }catch (Exception e){
                        e.printStackTrace ();
                    }

                }
            }).start ();
        }
        
    }
    private static Object object=new Object ();

    public static void testSynchronized1(){
        synchronized (object){
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T3 %d", i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void testSynchronized2(){
        synchronized (new Object()){
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T4 %d", i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void testSynchronized(){
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();

        }

    }
    public static void testBlockQueue(){
        BlockingQueue<String>q=new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q),"Consumer1").start();
        new Thread(new Consumer(q),"Consumer2").start();

    }
    private static ThreadLocal<Integer>threadLocalUserId=new ThreadLocal<>();
    private static int userId;
    public static void testThreadLocal(){
        for (int i = 0; i < 10; i++) {
            final int finali=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        threadLocalUserId.set(finali);//线程本地变量，均有自身副本
                        Thread.sleep(1000);
                        System.out.println("ThreadLocal:"+threadLocalUserId.get());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        for (int i = 0; i < 10; i++) {
            final int finali=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        userId=finali;//最后一条变量
                        Thread.sleep(1000);
                        System.out.println("UserId:"+userId);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    } 
    public static void testExecutor(){
//        ExecutorService service= Executors.newSingleThreadExecutor();
        ExecutorService service= Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {
            @Override
            public void run () {
                for (int i = 0; i < 10; ++i) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor1:" + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run () {
                for (int i = 0; i < 10; ++i) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor2:" + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.shutdown();
        while (!service.isTerminated()){
            try {
                Thread.sleep(1000);
                System.out.println("Wait for termination.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static int counter=0;
    private static AtomicInteger atomicInteger=new AtomicInteger(0);
    public static void testWithOutAtor(){
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run () {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; j++) {
                            counter++;
                            System.out.println(counter);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void testWithAtor(){
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run () {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; j++) {
                            System.out.println(atomicInteger.incrementAndGet());
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    public static void testFuture() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
//                Thread.sleep(1000);
                throw new IllegalArgumentException("异常");
//                return 1;
            }
        });

        service.shutdown();
        try {
            System.out.println(future.get());//等待call时间完成
//            System.out.println(future.get(100, TimeUnit.MILLISECONDS));//超时报错
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
//        testThread ();
//        testSynchronized();
//        testBlockQueue();
        testThreadLocal();
//        testExecutor();
//        testWithAtor();
//        testWithOutAtor();
//        testFuture();
    }


}

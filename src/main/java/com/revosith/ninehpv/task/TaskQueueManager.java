package com.revosith.ninehpv.task;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author Revosith
 * @description
 * @date 2020/12/17.
 */
@Slf4j
public class TaskQueueManager {
    /**
     * 线程池数量 默认 2
     */
    private static int POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    /**
     * 固定大小线程池
     */
    private static ExecutorService EXECUTOR = new ThreadPoolExecutor(100, 100, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
    /**
     * 延迟队列
     */
    private static DelayQueue<TaskDelayed> DELETE_DELAYEDS = new DelayQueue<>();

    private TaskQueueManager() {
        init();
    }

    private static class TaskQueueManagerHolder {
        private static TaskQueueManager INSTANCE = new TaskQueueManager();
    }

    /**
     * 获取实例
     *
     * @return RedisUtils
     */
    public static TaskQueueManager getInstance() {
        return TaskQueueManagerHolder.INSTANCE;
    }

    /**
     * 初始化
     */
    private void init() {
        log.info("延迟队列,初始化");
        Thread daemonThread = new Thread(this::execute);
        daemonThread.setName("延迟队列");
        daemonThread.start();
    }

    /**
     * 运行
     */
    private void execute() {
        while (true) {
            try {
                log.info("延迟队列,队列大小：{}", DELETE_DELAYEDS.size());
                //队列取值
                TaskDelayed taskDelayed = DELETE_DELAYEDS.take();

                if (taskDelayed.getTask() == null) {
                    continue;
                }
                //拿出任务
                Runnable runnable = taskDelayed.getTask();
                //将任务放到多线程执行删除文件
                EXECUTOR.execute(runnable);
            } catch (Exception e) {
                log.error("延迟处理失败，错误信息", e);
            }
        }
    }

    /**
     * 增加延迟任务
     * ms级别
     */
    public void put(Runnable task, Long delayTime) {
        //固定3分钟.
        DELETE_DELAYEDS.put(new TaskDelayed<>(delayTime, task));
    }
}
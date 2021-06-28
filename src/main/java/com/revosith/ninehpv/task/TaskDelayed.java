package com.revosith.ninehpv.task;

import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author Revosith
 * @description
 * @date 2020/12/17.
 */
@Data
public class TaskDelayed<T extends Runnable> implements Delayed {
    /**
     * 延迟时间
     */
    private Long delayTime;
    /**
     * 到期时间
     */
    private Long expire;
    /**
     * 任务
     */
    private T task;

    public TaskDelayed(long delayTime, T task) {
        this.task = task;
        this.delayTime = delayTime;
        this.expire = delayTime + System.currentTimeMillis();
    }

    /**
     * 剩余时间=到期时间-当前时间  convert: 将给定单元的时间段转换到此单元。
     *
     * @param unit unit
     * @return long
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 优先队列里面优先级规则  TimeUnit .MILLISECONDS 获取单位 为毫秒的时间戳
     *
     * @param o 所比较的队列
     * @return int
     */
    @Override
    public int compareTo(Delayed o) {
        TaskDelayed other = (TaskDelayed) o;
        long diff = this.expire - other.expire;
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
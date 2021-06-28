package com.revosith.ninehpv.task;

import com.revosith.ninehpv.service.RequestService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Revosith
 * @description
 * @date 2020/12/17.
 */
@Slf4j
public class KillTaskTask implements Runnable {

    private final RequestService service;

    private final String name;


    public KillTaskTask(RequestService killParam,String name) {
        this.service = killParam;
        this.name = name;
    }


    @Override
    public void run() {
        try {
            //不断重试吧。
            do {
                log.info("{}我开始啦。。",name);
                try {
                    service.secKill();
                }catch (Exception e){
                    log.error("失败啦",e);
                }
            } while (service.isContinue());
        } catch (Exception e) {
            log.error("发送异常。。。", e);
        }
    }
}
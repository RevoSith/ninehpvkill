package com.revosith.ninehpv.controller;

import com.alibaba.fastjson.JSON;
import com.revosith.ninehpv.dto.CookieParam;
import com.revosith.ninehpv.dto.KillParam;
import com.revosith.ninehpv.dto.UserDto;
import com.revosith.ninehpv.dto.VaccineDto;
import com.revosith.ninehpv.service.RequestService;
import com.revosith.ninehpv.task.KillTaskTask;
import com.revosith.ninehpv.task.TaskQueueManager;
import com.revosith.ninehpv.util.HeaderTransferUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author:
 * @data: 2021/6/16
 * @Description: 配置初始化
 **/
@Slf4j
@RestController
public class ConfInitController {

    /**
     * 提交任务
     *
     * @return
     */
    @PostMapping(value = "/subtask")
    public Boolean submitTask(@RequestParam String cookies,@RequestParam String name) throws Exception {
        CookieParam cookie = new CookieParam();
        cookie.setName(name);
        cookie.setCookies(cookies);
        //1解析cookie
        KillParam killParam = HeaderTransferUtil.parseHeader(cookie.getCookies());

        if (killParam == null) {
            log.info("解析失败。");
            return false;
        }
        //2获取请求对象信息
        RequestService requestService = new RequestService(killParam);
        UserDto userDto = requestService.pickUser(cookie.getName());
        killParam.setUserDto(userDto);
        //3获取疫苗信息
        VaccineDto vaccineDto = requestService.getVaccineDto();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (vaccineDto == null) {
            log.info("没有可以抢的疫苗。");
            return false;
        }

        Date date = format.parse(vaccineDto.getStartTime());
        long time2Kill = date.getTime() - System.currentTimeMillis();

        killParam.setVaccineDto(vaccineDto);
        //开始前两分钟的延时任务开启
        addTask(requestService,time2Kill-2*60*1000,"G1_");
        //开始前一分钟的延时任务开启
        addTask(requestService,time2Kill-60*1000,"G2_");
        //开始前30s的延时任务开启
        addTask(requestService,time2Kill-30*1000,"G3_");
        //开始前10s的延时任务开启
        addTask(requestService,time2Kill-10*1000,"G4_");
        //开始前2s的延时任务开启
        addTask(requestService,time2Kill-2*1000,"G5_");
        return true;
    }

    private void addTask(RequestService requestService, long delay,String group) {
        //20ms一个区间 5个任务
        //延时任务组
        for (int i = 0; i < 5; i++) {
            log.info("添加第{}组线程", i);
            TaskQueueManager.getInstance().put(new KillTaskTask(requestService,group+i), delay + i * 20);
        }
    }
}

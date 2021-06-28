package com.revosith.ninehpv.dto;

import lombok.Data;

/**
 * @author: hehaiyong@51talk.com
 * @data: 2021/6/16
 * @Description: 疫苗
 **/
@Data
public class VaccineDto {
    private Integer id;
    /**
     * 医院名称
     */
    private String name;
    /**
     * 医院地址
     */
    private String address;
    /**
     * 疫苗代码
     */
    private String vaccineCode;
    /**
     * 疫苗名称
     */
    private String vaccineName;
    /**
     * 秒杀时间
     */
    private String startTime;
}

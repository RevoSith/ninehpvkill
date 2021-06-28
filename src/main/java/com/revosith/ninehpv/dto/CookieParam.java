package com.revosith.ninehpv.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:
 * @data: 2021/6/16
 * @Description: cookie
 **/
@Data
public class CookieParam implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 会话cookie
     */
    private String cookies;
    /**
     * 抢苗的人
     */
    private String name;
}

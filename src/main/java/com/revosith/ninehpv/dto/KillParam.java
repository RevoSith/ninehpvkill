package com.revosith.ninehpv.dto;

import lombok.Data;

/**
 * @author:
 * @data: 2021/6/16
 * @Description: 请求参数
 **/
@Data
public class KillParam {

    private String tk;
    private String cookies;
    private UserDto userDto;
    private VaccineDto vaccineDto;
}

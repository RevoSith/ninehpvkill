package com.revosith.ninehpv.util;

import com.revosith.ninehpv.dto.KillParam;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: hehaiyong@51talk.com
 * @data: 2021/6/16
 * @Description:
 **/
public class HeaderTransferUtil {

    public static KillParam parseHeader(String reqHeader) {
        if (StringUtils.isEmpty(reqHeader)) {
            return null;
        }
        List<String> data = new ArrayList<>(2);
        reqHeader = reqHeader.replaceAll("cookie: ", "Cookie: ");
        int start = reqHeader.indexOf("tk: ");
        int end = reqHeader.indexOf("\n", start);
        if (start == -1 || end == -1) {
            return null;
        }
        KillParam param = new KillParam();
        param.setTk(reqHeader.substring(start + "tk: ".length(), end));
        start = reqHeader.indexOf("Cookie: ");
        end = reqHeader.indexOf("\n", start);
        if (start == -1 || end == -1) {
            return null;
        }
        param.setCookies(reqHeader.substring(start + "Cookie: ".length(), end));
        return param;
    }
}

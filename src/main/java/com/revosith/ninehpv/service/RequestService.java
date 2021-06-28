package com.revosith.ninehpv.service;

import com.alibaba.fastjson.JSONObject;
import com.revosith.ninehpv.dto.KillParam;
import com.revosith.ninehpv.dto.UserDto;
import com.revosith.ninehpv.dto.VaccineDto;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author:
 * @data: 2021/6/16
 * @Description:
 **/
public class RequestService {

    private final KillParam param;

    public RequestService(KillParam param) {
        this.param = param;
    }

    /**
     * 基础请求路径
     */
    private static final String BASE_URL = "https://miaomiao.scmttec.com";


    /**
     * 获取接种人信息
     *
     * @return
     */
    public UserDto pickUser(String name) throws Exception {
        String path = BASE_URL + "/seckill/linkman/findByUserId.do";
        String json = get(path, null, null);
        List<UserDto> tempList = JSONObject.parseArray(json, UserDto.class);
        return tempList.stream().filter(e -> name.equals(e.getName())).findFirst().orElse(null);
    }

    /**
     * 获取疫苗
     *
     * @return
     * @throws Exception
     */
    public VaccineDto getVaccineDto() throws Exception {
        String path = BASE_URL + "/seckill/seckill/list.do";
        Map<String, String> param = new HashMap<>();
        //九价疫苗的code
        param.put("offset", "0");
        param.put("limit", "100");
        //这个应该是成都的行政区划前四位
        param.put("regionCode", "5101");
        String json = get(path, param, null);
        List<VaccineDto> tempList = JSONObject.parseArray(json, VaccineDto.class);
        return CollectionUtils.isEmpty(tempList) ? null : tempList.get(0);
    }

    public String secKill() throws Exception {
        String path = BASE_URL + "/seckill/seckill/subscribe.do";
        VaccineDto vaccineDto = param.getVaccineDto();
        UserDto userDto = param.getUserDto();
        //请求组装
        Map<String, String> params = new HashMap<>();
        params.put("seckillId", vaccineDto.getId().toString());
        params.put("vaccineIndex", "1");
        params.put("linkmanId", userDto.getId().toString());
        params.put("idCardNo", userDto.getIdCardNo());
        //后面替换成接口返回的st
        //目前发现接口返回的st就是当前时间，后面可能会固定为一个加密参数
        long st = System.currentTimeMillis();
        Header header = new BasicHeader("ecc-hs", eccHs(vaccineDto.getId().toString(), st));
        return get(path, params, header);
    }

    private String get(String path, Map<String, String> params, Header extHeader) throws Exception {
        if (params != null && params.size() != 0) {
            StringBuilder paramStr = new StringBuilder("?");
            params.forEach((key, value) -> {
                paramStr.append(key).append("=").append(value).append("&");
            });
            String t = paramStr.toString();
            if (t.endsWith("&")) {
                t = t.substring(0, t.length() - 1);
            }
            path += t;
        }
        HttpGet get = new HttpGet(path);
        List<Header> headers = getCommonHeader();
        if (extHeader != null) {
            headers.add(extHeader);
        }
        get.setHeaders(headers.toArray(new Header[0]));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpEntity httpEntity = httpClient.execute(get).getEntity();
        String json = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        JSONObject jsonObject = JSONObject.parseObject(json);
        if ("0000".equals(jsonObject.get("code"))) {
            return jsonObject.getString("data");
        } else {
            throw new Exception(jsonObject.getString("msg"));
        }
    }

    private List<Header> getCommonHeader() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; SM-N960F Build/JLS36C; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/74.0.3729.136 Mobile Safari/537.36 MMWEBID/1042 MicroMessenger/7.0.15.1680(0x27000F34) Process/appbrand0 WeChat/arm32 NetType/WIFI Language/zh_CN ABI/arm32"));
        headers.add(new BasicHeader("Referer", "https://servicewechat.com/wxff8cad2e9bf18719/2/page-frame.html"));
        headers.add(new BasicHeader("tk", param.getTk()));
        headers.add(new BasicHeader("Accept", "application/json, text/plain, */*"));
        headers.add(new BasicHeader("Host", "miaomiao.scmttec.com"));
        headers.add(new BasicHeader("Cookie", param.getCookies()));
        return headers;
    }

    private String eccHs(String seckillId, Long st) {
        String salt = "ux$ad70*b";
        final Integer memberId = param.getUserDto().getId();
        String md5 = DigestUtils.md5Hex(seckillId + st + memberId);
        return DigestUtils.md5Hex(md5 + salt);
    }

    public boolean isContinue() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(param.getVaccineDto().getStartTime());
            return System.currentTimeMillis() < date.getTime() + 1000 * 60 * 2;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}

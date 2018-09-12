package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import util.CookieUtil;
import util.JwtUtil;
import util.WebConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {
@Reference
private UserInfoService userInfoService;

    @RequestMapping("index")
    public String index(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        // 保存上
        request.setAttribute("originUrl",originUrl);
        return "index";
    }

    @Value("${token.key}")
    String signKey;

    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response, UserInfo userInfo){
        // 取得ip地址
        String remoteAddr  = request.getHeader("X-forwarded-for");
        if (userInfo!=null) {
            UserInfo loginUser = userInfoService.login(userInfo);
            if (loginUser == null) {
                return "fail";
            } else {
                // 生成token
                Map map = new HashMap();
                map.put("userId", loginUser.getId());
                map.put("nickName", loginUser.getNickName());
                String token = JwtUtil.encode(signKey, map, remoteAddr);
                CookieUtil.setCookie(request,response,"token",token, WebConst.COOKIE_MAXAGE,false);
                return token;
            }
        }
        return "fail";
    }


    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request){
        //从参数中获取到token，和服务器的ip
        String token = request.getParameter("token");
        String currentIp = request.getParameter("currentIp");
        Map<String, Object> decode = JwtUtil.decode(token, signKey, currentIp);
        if(decode!=null){
            String userId = (String) decode.get("userId");
            UserInfo userInfo = userInfoService.verify(userId);
            if(userInfo!=null){
                return "success";
            }
        }
        return "fail";



    }
}

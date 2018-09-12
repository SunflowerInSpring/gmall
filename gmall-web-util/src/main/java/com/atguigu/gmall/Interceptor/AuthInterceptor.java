package com.atguigu.gmall.Interceptor;

import com.atguigu.gmall.cofing.LoginRequire;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import util.CookieUtil;
import util.HttpClientUtil;
import util.WebConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import static util.WebConst.COOKIE_MAXAGE;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("newToken");
        if(token!=null){
            CookieUtil.setCookie(request,response,"token",token,COOKIE_MAXAGE,false);
        }
        if(token==null){
            token = CookieUtil.getCookieValue(request, "token", false);
        }
        if(token!=null){
            Map map =  getUserMapByToken(token);
            String  nickName = (String) map.get("nickName");
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token );
            if ("success".equals(result)){
                String userId = (String) map.get("userId");
                request.setAttribute("userId", userId);
            }
            request.setAttribute("nickName",nickName);
        }

        HandlerMethod handlerMethod =(HandlerMethod) handler;
        LoginRequire methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if(methodAnnotation!=null) {
            String header = request.getHeader("x-forwarded-for");
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&currentIp=" + header);
            if ("success".equals(result)) {
                Map map = getUserMapByToken(token);
                String userId = (String) map.get("userId");
                request.setAttribute("userId", userId);
                return true;
            } else {
                if (methodAnnotation.autoRedirect()) {
                    String requestURL = request.getRequestURL().toString();
                    String encodeURL = URLEncoder.encode(requestURL, "UTF-8");
                    response.sendRedirect(WebConst.LOGIN_ADDRESS + "?originUrl=" + encodeURL);
                    return false;
                }
            }
        }
        return true;
    }


    private Map getUserMapByToken(String token) {
        String tokenUserInfo = StringUtils.substringBetween(token, ".");
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] decode = base64UrlCodec.decode(tokenUserInfo);
        String tokenJson = null;
        try {
            tokenJson = new String(decode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map map = JSON.parseObject(tokenJson, Map.class);
        return map;

    }
}

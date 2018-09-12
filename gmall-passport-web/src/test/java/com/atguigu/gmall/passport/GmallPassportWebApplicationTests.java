package com.atguigu.gmall.passport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPassportWebApplicationTests {

	@Test
	public void contextLoads() {

		String key = "atguigu";
		String ip="192.168.67.201";
		Map map = new HashMap();
		map.put("userId","1001");
		map.put("nickName","marry");
		String token = JwtUtil.encode(key, map, ip);
		System.out.println(token);
		Map<String, Object> decode = JwtUtil.decode(token, key, "192.168.67.201");
		System.out.println(decode);
	}


}

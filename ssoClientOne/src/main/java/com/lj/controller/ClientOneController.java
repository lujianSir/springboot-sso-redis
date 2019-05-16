package com.lj.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/one")
public class ClientOneController {

	@Autowired
    private RedisTemplate redisTemplate;
	
	@RequestMapping("/index")
    public String index(ModelMap map,HttpServletRequest request){   
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
        map.addAttribute("age","15");  
        String token="";
        Cookie[] cs=request.getCookies(); 
        for (Cookie cookie : cs) {
			if(cookie.getName().equals("token")) {
				token=cookie.getValue();
			}
		}
        
        boolean hasKey = redisTemplate.hasKey(token);
        String name="";
        if(hasKey) {
        	name=operations.get(token);
        }
        map.addAttribute("name",name);
        return "index";
    }
	
}

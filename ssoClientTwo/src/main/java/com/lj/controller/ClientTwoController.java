package com.lj.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/one")
public class ClientTwoController {

	@RequestMapping("/index")
    public String index(ModelMap map,HttpServletRequest request){
        map.addAttribute("age","15");
        String name="";
        Cookie[] cs=request.getCookies(); 
        for (Cookie cookie : cs) {
			if(cookie.getName().equals("name")) {
				name=cookie.getValue();
			}
		}
        map.addAttribute("name",name);
        return "index";
    }
	
}

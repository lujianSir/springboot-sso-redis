package com.lj.controller;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lj.entity.User;

@Controller
@RequestMapping("/sso")
public class LoginController {

	@Autowired
    private RedisTemplate redisTemplate;
	
	@RequestMapping("/login")
	public String login(HttpSession session,String name, String password,String callbackURL,@ModelAttribute(value="user") User user,HttpServletRequest request,ModelMap map) {			
//		if(session.getAttribute("login")!=null) {
//			if((boolean) session.getAttribute("login")) {	
//				return "redirect:/sso/returnUrl?callbackURL="+callbackURL;
//			}
//		}
		
		ValueOperations<String, String> operations = redisTemplate.opsForValue();		
		if(name == null && password == null) {
			String callbackURL1 = request.getRequestURL().toString(); 
			map.addAttribute("callbackURL",callbackURL);
			return "login";
		} 
		if("admin".equals(name) && "admin".equals(password)||"zhangsan".equals(name) && "123456".equals(password)) {
			String callbackURL1 = request.getRequestURL().toString(); 
			String token = UUID.randomUUID().toString();
		//	session.setAttribute("login", true);
		    session.setAttribute("token", token);
			// 写入缓存
            operations.set(token, name, 5, TimeUnit.HOURS);          
			return "redirect:/sso/returnUrl?callbackURL="+callbackURL+"&name="+name;
		} else {
			String callbackURL1 = request.getRequestURL().toString(); 
			map.addAttribute("callbackURL",callbackURL);
			return "login";
		}
	}
	
	
	@RequestMapping("/returnUrl")
	public void jumpReturnUrl(HttpServletRequest request,HttpServletResponse response,String callbackURL,String name) throws IOException {	
		String token=(String) request.getSession().getAttribute("token");
		Cookie c=new Cookie("name", name);
		c.setPath("/");
		response.addCookie(c);
		Cookie c1=new Cookie("token", token);
		c1.setPath("/");
		response.addCookie(c1);	
		//response.sendRedirect(callbackURL+"?token="+token);
		response.sendRedirect(callbackURL);
	}
	
	@RequestMapping("verify")
	@ResponseBody
	public JSONObject verify(HttpServletRequest request, @RequestParam String token) {
		HttpSession session = request.getSession();
		JSONObject result = new JSONObject();
		if(session.getAttribute("token") != null && token.equals(session.getAttribute("token"))) {
			result.put("code", "success");
			result.put("msg", "认证成功");
		} else {
			result.put("code", "failure");
			result.put("msg", "token已失效，请重新登录！");
		}
		return result;
	}
	
//	@RequestMapping("/logout")
//    @ResponseBody
//    public JSONObject logOut(HttpServletRequest request){
//        JSONObject jsonObject = new JSONObject();
//        HttpSession session = request.getSession();
//        if (session == null){
//
//        }else {
//            session.invalidate();
//            jsonObject.put("info","注销成功");
//            jsonObject.put("code", HttpStatus.OK);
//        }
//        return jsonObject;
//    }
	@RequestMapping("/logout")
    public String logOut(HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        HttpSession session = request.getSession();
        if (session == null){

        }else {
            session.invalidate();
            jsonObject.put("info","注销成功");
            jsonObject.put("code", HttpStatus.OK);
        }
        return "login";
    }
}

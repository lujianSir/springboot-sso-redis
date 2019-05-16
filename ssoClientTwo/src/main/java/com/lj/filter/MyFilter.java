package com.lj.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.lj.utils.RestTemplateUtil;

public class MyFilter implements Filter{

	//sso认证
	private final  String SSO_SERVER_URL="http://localhost:8090/sso/login";
	//sso token 验证
	private final String SSO_SERVER_VERIFY_URL="http://localhost:8090/sso/verify";
	//sso注销
    private final String SSO_LOGINOUT_URL="http://localhost:8090/sso/logout";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		System.out.println("服务启动,调用过滤器Filter初始化方法init()..........");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		 HttpServletRequest request = (HttpServletRequest) req;
		 HttpServletResponse response = (HttpServletResponse) res;
		 //拦截token
		 String cookieToken="";
		 Cookie[] cs=request.getCookies(); 
		 if(cs!=null &&cs.length>0) {
			for (Cookie cookie : cs) {
				if(cookie.getName().equals("token")) {
					cookieToken=cookie.getValue();
				}	
			}
		 }   
		 
		//获取注销的标记销毁全局会话
        String loginOut = request.getParameter("logout");
        if (loginOut!=null&&loginOut.equals("true")){
        	String callbackURL = request.getRequestURL().toString();
        	response.sendRedirect(SSO_LOGINOUT_URL+"?callbackURL="+callbackURL);
        	return;
        }	
		 
		 //拦截token
		 String token = request.getParameter("token");
		 if(!cookieToken.equals("")&&token==null) {
			 token=cookieToken;
		 }
		 if(token != null) {
			 boolean flag = this.verify(request, SSO_SERVER_VERIFY_URL, token);
			 if (flag) {
			    	chain.doFilter(req, res);
			        return;
			    } else {	;
		             HttpSession session = request.getSession();
		             session.invalidate();             
			    }
		 }
		 	 
      //没有token就跳转到认证中心
        //当前请求地址
	  String callbackURL = request.getRequestURL().toString(); 
	  StringBuilder url = new StringBuilder();
	  url.append(SSO_SERVER_URL).append("?callbackURL=").append(callbackURL); 
	  response.sendRedirect(url.toString());		 
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("服务关闭，调用过滤器Filter的销毁方法destroy()..........");
	}

	private boolean verify(HttpServletRequest request, String verifyUrl, String token) {
		String result = RestTemplateUtil.get(request, verifyUrl + "?token=" + token, null);
		JSONObject ret = JSONObject.parseObject(result);
		if("success".equals(ret.getString("code"))) {
			return true;
		}else {
			return false;
		}	
	}
}

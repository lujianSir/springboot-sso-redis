package com.lj.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class WebConfig {

	@Bean
	   public FilterRegistrationBean filterRegistrationBean() {

	      FilterRegistrationBean registrationBean = new FilterRegistrationBean();
	      MyFilter filter = new MyFilter();
	      registrationBean.setFilter(filter);
	      //设置过滤器拦截请求
	      List<String> urls = new ArrayList<>();
	      urls.add("/*");
	      registrationBean.setUrlPatterns(urls);
	      return registrationBean;
	   }

}

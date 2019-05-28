package com.sugon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sugon.constant.Const;

@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.sugon.*"})
@Controller
public class Application {
	
	@Autowired
	private Const c;
	
	@RequestMapping("index")
	public String index(){
		System.out.println(c.test);
		return "index";
	}
	
    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);
    }
    
}
package com.pragma.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class WebappController{
	private Logger log = Logger.getLogger(getClass());
		@RequestMapping(value = "/")
	public String main(HttpServletRequest request) throws UnsupportedEncodingException {		
		return "index";
	}
		
		

}
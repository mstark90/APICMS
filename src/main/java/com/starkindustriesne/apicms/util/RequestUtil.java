/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.util;

import com.starkindustriesne.apicms.domain.AccessKey;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author michaelstark
 */
public class RequestUtil {
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest();
    }
    
    public static String getClientKey() {
        return getRequest().getHeader("X-Client-Key");
    }
    
    public static String getClientSecret() {
        return getRequest().getHeader("X-Client-Secret");
    }
    
    
    public static String getSessionToken() {
        return getRequest().getHeader("X-Session-Token");
    }
    
    public static AccessKey getCurrentKey() {
        return (AccessKey)getRequest().getAttribute("accessKey");
    }
}

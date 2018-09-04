package com.mikewoo.server.utils;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json数据处理工具类
 * @author Eric Gui
 * @date 2018/9/4
 */
public class JsonUtil {
	
	private static final ObjectMapper objectMapper = new ObjectMapper(); 
	
    
    public static <T> String bean2Json(T bean) {  
        try {
        	objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.writeValueAsString(bean);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return "";  
    }  
      
    public static String map2Json(Map<?, ?> map) {  
        try {
        	objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.writeValueAsString(map);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return "";  
    }  
      
    public static String list2Json(List<?> list) {  
        try {  
        	objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.writeValueAsString(list);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return "";  
    }  
      
    public static <T> T json2Bean(String json, Class<T> beanClass) {  
        try {  
        	objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(json, beanClass);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  

    public static Map<?, ?> json2Map(String json, TypeReference<?> type) {  
        try {  
        	objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
            return (Map<?, ?>)objectMapper.readValue(json, type);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }
   
}

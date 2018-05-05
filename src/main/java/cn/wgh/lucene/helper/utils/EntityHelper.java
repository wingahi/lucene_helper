package cn.wgh.lucene.helper.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import cn.wgh.lucene.helper.annotation.IndexField;
import cn.wgh.lucene.helper.entity.EntityValueMapper;

public final class EntityHelper {

	public static Map<String,Class> analyzeEntity(Object entity) {
		Map<String,Class> fieldMap = new HashMap<String,Class>();
		String result = "";
		int beginnumber = 0;
		int endnumber = 0;

		try {

			if (entity == null) {

			} else {
				Field[] fields = entity.getClass().getDeclaredFields();
				Method[] menthods = entity.getClass().getMethods();
				List param = new ArrayList();
				for (int i = 0; menthods != null && i < menthods.length; i++) {
					menthods[i].setAccessible(true); // 暴力反射
					String column = menthods[i].getName();
					if ((!column.equals("getClass")) && column.contains("get")) {
						Object value = menthods[i].invoke(entity,
								new Object[] {});
						Type type = menthods[i].getGenericReturnType();
						String classtype = type.toString()
								.replace("class ", "");
						// 用于强制转换
						Class cls = Class.forName(classtype);

						String method = menthods[i].getName();
						method = method.replace("get", "");
						method = method.substring(0,1).toLowerCase()+method.substring(1);
						fieldMap.put(method, cls);
					}
				}

			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return fieldMap;
	}
	
	public static Map<String,EntityValueMapper> analyzeEntityValue(Object entity) {
		Map<String,EntityValueMapper> fieldMap = new HashMap<String,EntityValueMapper>();
		String result = "";
		int beginnumber = 0;
		int endnumber = 0;
		try {
			if (entity == null) {

			} else {
				Field[] fields = entity.getClass().getDeclaredFields();
				Method[] menthods = entity.getClass().getMethods();
				EntityValueMapper evMapper = null;
				for (int i = 0; menthods != null && i < menthods.length; i++) {
					menthods[i].setAccessible(true); // 暴力反射
					String column = menthods[i].getName();
					if ((!column.equals("getClass")) && column.contains("get")) {
						Object value = menthods[i].invoke(entity,
								new Object[] {});
						evMapper = new EntityValueMapper();
						Type type = menthods[i].getGenericReturnType();
						IndexField indexField = menthods[i].getAnnotation(IndexField.class);
						if(indexField!=null){
							String method = menthods[i].getName();
							method = method.replace("get", "");
							method = method.substring(0,1).toLowerCase()+method.substring(1);
							evMapper.setIndex(indexField.index());
							evMapper.setStore(indexField.store());
							evMapper.setValue(value);
						    fieldMap.put(method, evMapper);
					    }
			
					}
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return fieldMap;
	}
	
	// Map --> Bean 2: 利用org.apache.commons.beanutils 工具类实现 Map --> Bean  
    public static void transMap2Bean2(Map<String, Object> map, Object obj) {  
        if (map == null || obj == null) {  
            return;  
        }  
        try {  
            BeanUtils.populate(obj, map);  
        } catch (Exception e) {  
            System.out.println("transMap2Bean2 Error " + e);  
        }  
    }  
  
    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean  
    public static void transMap2Bean(Map<String, Object> map, Object obj) {  
  
        try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
  
            for (PropertyDescriptor property : propertyDescriptors) {  
                String key = property.getName();  
  
                if (map.containsKey(key)) {  
                    Object value = map.get(key);  
                    // 得到property对应的setter方法  
                    Method setter = property.getWriteMethod();  
                    setter.invoke(obj, value);  
                }  
  
            }  
  
        } catch (Exception e) {  
            System.out.println("transMap2Bean Error " + e);  
        }  
  
        return;  
  
    }  
  
    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map  
    public static Map<String, Object> transBean2Map(Object obj) {  
  
        if(obj == null){  
            return null;  
        }          
        Map<String, Object> map = new HashMap<String, Object>();  
        try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
            for (PropertyDescriptor property : propertyDescriptors) {  
                String key = property.getName();  
  
                // 过滤class属性  
                if (!key.equals("class")) {  
                    // 得到property对应的getter方法  
                    Method getter = property.getReadMethod();  
                    Object value = getter.invoke(obj);  
  
                    map.put(key, value);  
                }  
  
            }  
        } catch (Exception e) {  
            System.out.println("transBean2Map Error " + e);  
        }  
  
        return map;  
  
    }  
}

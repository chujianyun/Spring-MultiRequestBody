package io.github.chujianyun.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.chujianyun.annotation.MultiRequestBody;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * MultiRequestBody解析器
 * 解决的问题：
 * 1、单个字符串等包装类型都要写一个对象才可以用@RequestBody接收；
 *
 * 2、多个对象需要封装到一个对象里才可以用@RequestBody接收。
 * 本功能的作用：
 * 1、支持通过注解的value指定JSON的key来解析对象。
 *
 * 2、支持通过注解无value，直接根据参数名来解析对象
 *
 * 3、支持通过注解无value且参数名不匹配JSON串key时，根据属性解析对象。
 *
 * 4、支持多余属性(不解析、不报错)、支持参数“共用”（不指定value时，参数名不为JSON串的key）
 *
 * 5、支持当value和属性名找不到匹配的key时，对象是否匹配所有属性。
 * @author Wangyang Liu
 * @date 2018/08/27
 */
public class MultiRequestBodyArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String JSONBODY_ATTRIBUTE = "JSON_REQUEST_BODY";

    /**
     * 设置支持的方法参数类型
     *
     * @param parameter 方法参数
     * @return 支持的类型
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 支持带@MultiRequestBody注解的参数
        return parameter.hasParameterAnnotation(MultiRequestBody.class);
    }

    /**
     * 参数解析，利用fastjson
     * 注意：非基本类型返回null会报空指针异常，要通过反射或者JSON工具类创建一个空对象
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String jsonBody = getRequestBody(webRequest);

        JSONObject jsonObject = JSON.parseObject(jsonBody);
        // 根据@MultiRequestBody注解value作为json解析的key
        MultiRequestBody parameterAnnotation = parameter.getParameterAnnotation(MultiRequestBody.class);
        //注解的value是JSON的key
        String key = parameterAnnotation.value();
        Object value;
        // 如果@MultiRequestBody注解没有设置value，则取参数名FrameworkServlet作为json解析的key
        if (StringUtils.isNotEmpty(key)) {
            value = jsonObject.get(key);
            // 如果设置了value但是解析不到，报错
            if (value == null && parameterAnnotation.required()) {
                throw new IllegalArgumentException(String.format("required param %s is not present", key));
            }
        } else {
            // 注解为设置value则用参数名当做json的key
            key = parameter.getParameterName();
            value = jsonObject.get(key);
        }

        // 获取的注解后的类型 Long
        Class<?> parameterType = parameter.getParameterType();
        // 通过注解的value或者参数名解析，能拿到value进行解析
        if (value != null) {
            return JSON.parseObject(value.toString(), parameterType);
        }

        // 解析不到则将整个json串解析为当前参数类型
        if (isBasicDataTypes(parameterType)) {
            if (parameterAnnotation.required()) {
                throw new IllegalArgumentException(String.format("required param %s is not present", key));
            } else {
                return null;
            }
        }

        Object result = parameterType.newInstance();
        // 非基本类型，不允许解析所有字段，返回null
        if (!parameterAnnotation.parseAllFields()) {
            // 如果是必传参数抛异常
            if (parameterAnnotation.required()) {
                throw new IllegalArgumentException(String.format("required param %s is not present", key));
            }
            // 否则返回空对象
            return result;
        }
       // 非基本类型，允许解析，将外层属性解析
        result = JSON.parseObject(jsonObject.toString(), parameterType);
        // 如果非必要参数直接返回，否则如果没有一个属性有值则报错
        if (!parameterAnnotation.required()) {
                return result;
        }else{
            boolean haveValue = false;
            Field[] declaredFields = parameterType.getDeclaredFields();
            for(Field field : declaredFields){
                field.setAccessible(true);
                if(field.get(result) != null){
                    haveValue = true;
                    break;
                }
            }
            if(!haveValue){
                throw new IllegalArgumentException(String.format("required param %s is not present", key));
            }
            return result;
        }
    }

    /**
     * 基本数据类型直接返回
     */
    private boolean isBasicDataTypes(Class clazz) {
        Set<Class> classSet = new HashSet<>();
        classSet.add(String.class);
        classSet.add(Integer.class);
        classSet.add(Long.class);
        classSet.add(Short.class);
        classSet.add(Float.class);
        classSet.add(Double.class);
        classSet.add(Boolean.class);
        classSet.add(Character.class);
        return classSet.contains(clazz);
    }

    /**
     * 获取请求体JSON字符串
     */
    private String getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        // 有就直接获取
        String jsonBody = (String) webRequest.getAttribute(JSONBODY_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        // 没有就从请求中读取
        if (jsonBody == null) {
            try {
                jsonBody = IOUtils.toString(servletRequest.getReader());
                webRequest.setAttribute(JSONBODY_ATTRIBUTE, jsonBody, NativeWebRequest.SCOPE_REQUEST);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonBody;
    }
}

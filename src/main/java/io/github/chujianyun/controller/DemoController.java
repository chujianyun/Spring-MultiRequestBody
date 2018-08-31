package io.github.chujianyun.controller;


import io.github.chujianyun.annotation.MultiRequestBody;
import io.github.chujianyun.domain.Dog;
import io.github.chujianyun.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller Demo
 * @author Wangyang Liu
 * @date 2018/08/27
 */
@Controller
@RequestMapping("/xhr/test")
public class DemoController {

    @RequestMapping("/demo")
    @ResponseBody
    public String multiRequestBodyDemo1(@MultiRequestBody Dog dog, @MultiRequestBody User user) {
        System.out.println(dog.toString() + user.toString());
        return dog.toString() + ";" + user.toString();
    }


    @RequestMapping("/demo2")
    @ResponseBody
    public String multiRequestBodyDemo2(@MultiRequestBody("dog") Dog dog, @MultiRequestBody User user) {
        System.out.println(dog.toString() + user.toString());
        return dog.toString() + ";" + user.toString();
    }

    @RequestMapping("/demo3")
    @ResponseBody
    public String multiRequestBodyDemo3(@MultiRequestBody("dog") Dog dog, @MultiRequestBody("user") User user) {
        System.out.println(dog.toString() + user.toString());
        return dog.toString() + ";" + user.toString();
    }

    @RequestMapping("/demo4")
    @ResponseBody
    public String multiRequestBodyDemo4(@MultiRequestBody("dog") Dog dog, @MultiRequestBody Integer age) {
        System.out.println(dog.toString() + age.toString());
        return dog.toString() + ";age属性为："+age.toString();
    }


    @RequestMapping("/demo5")
    @ResponseBody
    public String multiRequestBodyDemo5(@MultiRequestBody("color") String color, @MultiRequestBody("age") Integer age) {
        return "color="+color + "; age=" + age;
    }

    @RequestMapping("/demo6")
    @ResponseBody
    public String multiRequestBodyDemo6(@MultiRequestBody("dog") Dog dog, @MultiRequestBody Integer age) {
        System.out.println(dog.toString() + age.toString());
        return dog.toString() + ";age属性为："+age.toString();
    }


    @RequestMapping("/demo7")
    @ResponseBody
    public String multiRequestBodyDemo7(@MultiRequestBody(required = false) Dog dog, @MultiRequestBody("age") Integer age) {
        return "dog="+dog + "; age=" + age;
    }


    @RequestMapping("/demo10")
    @ResponseBody
    public String multiRequestBodyDemo10( @MultiRequestBody(parseAllFields = false,required = false) Dog dog) {
        return dog.toString();
    }
}

# Spring-MultiRequestBody
## 项目主要功能
为Spring多@RequestBody支持，来解决Controller中POST方式JSON格式请求时
1. 无法直接用@RequestBody解析基本类型包装类的问题。
2. 无法使用@RequestBody接收多个实体的问题。

## 项目优势
1. 支持通过注解的value指定JSON的key来解析对象。
2. 支持通过注解无value，直接根据参数名来解析对象
3. 支持通过注解无value且参数名不匹配JSON串key时，根据属性解析对象。
4. 支持多余属性(不解析、不报错)、支持参数“共用”（不指定value时，参数名不为JSON串的key）
5. 支持当value和属性名找不到匹配的key时，对象是否匹配所有属性。

## 参考资料
* 对应博文： [https://blog.csdn.net/w605283073/article/details/82119284](明明如月小角落)
* 其他参考：[https://stackoverflow.com/questions/12893566/passing-multiple-variables-in-requestbody-to-a-spring-mvc-controller-using-ajax](StackOverFlow讨论)



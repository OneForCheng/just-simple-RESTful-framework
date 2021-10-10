# just-simple-RESTful-framework

### 简介

`just-simple-RESTful-framework` 就是一个库，也是一个框架，提供如下功能：

- 这个库可以起一个 web 服务（可以接受 HTTP 请求）
- 这个库提供了依赖注入的功能（类似 spring，用之前写的 `InjectContainer` 来实现）
- 这个库提供了写 RESTful 风格的 API 的基础能力（类似 JAX-RS）
- 这个库可以把 RESTful 配置解析成相应的 url mapping（转化成路由映射表）

### 功能任务列表

实现最简单的 Happy Path 功能，不考虑请求方式以及各种参数的解析：

- [x] 启动http服务
- [x] 定义注解，满足RESTful路由常规配置
- [x] 使用注解，写RESTful路由的样例
- [x] 解析RESTResource注解，获取所有路由配置
- [x] 将路由配置生成可以解析的路由映射表
- [ ] 解析请求路由，与路由映射表做匹配，获取请求结果
    - [ ] 匹配成功，返回请求结果（200）
    - [x] 匹配不到，返回找不到资源的错误（404）
    - [x] 匹配过程中报错，返回服务内部错误（500）


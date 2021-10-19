# just-simple-RESTful-framework

### 简介

`just-simple-RESTful-framework` 就是一个库，也是一个框架，提供如下功能：

- 这个库可以起一个 web 服务（可以接受 HTTP 请求）
- 这个库提供了依赖注入的功能（类似 Spring，用之前写的 `InjectContainer` 来实现）
- 这个库提供了写 RESTful 风格的 API 的基础能力（类似 JAX-RS）
- 这个库可以把 RESTful 配置解析成相应的 url mapping（转化成路由映射表）

### 功能任务列表

一步步实现各种 happy path 功能：

- [x] 启动http服务
- [x] 定义注解，满足RESTful路由常规配置
- [x] 使用注解，写RESTful路由的样例
- [x] 解析RESTResource注解，获取所有路由配置
- [x] 将路由配置生成可以解析的路由映射表
- [x] 解析请求路由，与路由映射表做匹配，获取请求结果
    - [x] 匹配成功，返回请求结果（200）
    - [x] 匹配不到，返回找不到资源的错误（404）
    - [x] 匹配过程中报错，返回服务内部错误（500）
- [x] 支持对别名的依赖注入
- [x] 解析请求参数
  - [x] 支持查询参数（QueryParam）
  - [ ] 支持路径参数（PathParam）
  - [ ] 支持Body参数（RequestBody）
- [ ] 用 Composite pattern 重构递归算法（面向对象的递归）
- [ ] 添加测试（覆盖核心的解析代码以及简单的功能测试）
- [ ] 获取请求上下文（Context： Request & Request）
- [ ] 支持只返回 sub resource 类型的解析

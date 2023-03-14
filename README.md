# 功能说明

> 基于 springBoot 实践一些特性

# 结构

| 路径                      | 功能         |
|-------------------------|------------|
| com.example.annotation  | 自定义注解模块    |
| com.example.config      | 配置模块       |
| com.example.contant     | 常量模块       |
| com.example.controller  | contrller层 |
| com.example.datasource  | 数据源配置模块    |
| com.example.model      | 实体类模块      |
| com.example.exception   | 自定义异常      |
| com.example.factory     | 工厂模块       |
| com.example.handler     | 处理器模块      |
| com.example.interceptor | 拦截器模块      |
| com.example.listener    | 监听器模块      |
| com.example.mapper      | dao层       |
| com.example.runner      | 自动启动模块     |
| com.example.schedule    | 定时任务模块     |
| com.example.service     | service层   |
| com.example.tool       | 工具类        |
| demo.jedis              | jedis demo |
| likou                   | 力扣习题       |

# 准备工作
* 1.安装 MongoDB
* 2.安装 MySQL
* 3.安装 Redis
* 4.安装 ElasticSearch

# 已实践功能

* 1.参数验证
* 2.全局异常处理,全局返回json,支持多个参数传入_格式
* 3.动态数据源切换
* 4.读取 yaml 配置
* 5.mybatis sql 打印,数据库字段 1,2 映射为 java List<Integer>
* 6.替代 spring web 传统的 ContextloaderListener,实现初始化监听器 ServletContextListener
* 7.通过拦截器,实现接口请求登录拦截
* 8.通过 实现 ApplicationRunner,让 springboot启动后自动执行一些初始化
* 9.通过 Secduled 注解实现定时调度
* 10.与 MongoDB 交互
* 11.与 Elasticsearch 交互
* 12.与 redis 交互
* 13.Jackson 工具类
* 14.支持轻量级容器 Undertow,以及 https
* 15.获取代码内所有接口URI
* 16.异步测试
* 17.监听器实现异步观察者
* 18.shedlock 分布式锁

# 待实践功能

* 1.gossip 协议

# 常态化巩固

- 设计模式: http://c.biancheng.net/design_pattern/
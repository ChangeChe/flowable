# SpringBoot-Flowable
本项目整合了springboot2.3.2与flowable6.7.0，集成了`flowable-ui`并提供了修改用户鉴权的入口。

对于大多数流程里需要任务分配以及使用过程中可能会涉及到的多数据源切换也给出了可行的技术方案，详细介绍请看博文。

## 博客地址
https://blog.csdn.net/ooaash/article/details/120724635

## 使用说明
- 填充好数据库信息就可启动该工程。
- 由于用户认证是自定义的，当前工程是任意用户都能登录。
- 可重新编写`com.github.changeche.flowable.service.impl`下的类，只需实现对应接口就可以了。
- 集成后，项目可做普通http/rpc服务端对外提供服务。主要是利用`flowable-api`对审批流做一些操作。`com.github.changeche.flowable.service.impl.ProcessServiceImpl.start`是使用`flowable-api`的小demo。
- 项目中的`biz`表示业务，可以替换为任意名称
- 如果修改了`package`路径里的`biz`，需注意修改`com.github.changeche.flowable.common.config.MybatisPlusConfig.sqlSessionFactory`下`53`行的路径以及启动类里`@MapperScan`里的路径。
- 很多类文件里的`TODO`，表示此处需根据实际业务来处理逻辑




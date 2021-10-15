# 工程简介
[查看博客](https://blog.csdn.net/ooaash/article/details/120724635)

填充好数据库信息就可启动该工程。

由于用户认证是自定义的，当前工程是任意用户都能登录。

可重新编写`com.github.changeche.flowable.service.impl`下的类，只需实现对应接口就可以了。

# 说明
## `biz`
`biz`表示业务，可以替换为任意名称。

如果修改了`package`路径里的`biz`，需注意修改`com.github.changeche.flowable.common.config.MybatisPlusConfig.sqlSessionFactory`下`53`行的路径以及启动类里`@MapperScan`里的路径。
## TODO
很多类文件里的`TODO`，表示此处需根据实际业务来处理逻辑




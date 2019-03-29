# KTZS
## 基于spring boot和layui的课堂助手后台管理系统
### 项目描述
毕业设计作品，基于Android的教师课堂智能助手 的后台系统，基于spring boot、thymeleaf、layui等框架技术实现
### 实现功能
1. 课堂扫码签到
2. 教师发布通知
3. 课堂作业发布与上传下载
4. 学生请假
5. 各种记录查询
### 主要技术
1. redis数据库，实现session共享以及高速缓存，预防高并发
2. 通过webscoket建立客户端与服务端之间的长连接，保证消息推送实时性
3. 通过spring boot拦截器实现权限管理
4. spring data jpa
5. nginx服务器反向代理实现负载均衡

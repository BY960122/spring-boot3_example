package demo.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/***
 * @author: BYDylan
 * @date: 2022/2/21
 * @description: redis key demo
 */
public class KeyDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyDemo.class);
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);

    public static void main(String[] args) {
        LOGGER.info("清空数据: {}", jedis.flushDB());
        LOGGER.info("判断某个键是否存在: {}", jedis.exists("username"));
        LOGGER.info("新增<'username','kuangshen'>的键值对: {}", jedis.set("username", "kuangshen"));
        LOGGER.info("新增<'password','password'>的键值对: {}", jedis.set("password", "password"));
        LOGGER.info("系统中所有的键如下: {}", jedis.keys("*").toString());
        LOGGER.info("删除键password:", jedis.del("password"));
        LOGGER.info("判断键password是否存在: {}", jedis.exists("password"));
        LOGGER.info("查看键username所存储的值的类型: {}", jedis.type("username"));
        LOGGER.info("随机返回key空间的一个: {}", jedis.randomKey());
        LOGGER.info("重命名key: {}", jedis.rename("username", "name"));
        LOGGER.info("取出改后的name: {}", jedis.get("name"));
        LOGGER.info("按索引查询: {}", jedis.select(0));
        LOGGER.info("删除当前选择数据库中的所有key: {}", jedis.flushDB());
        LOGGER.info("返回当前数据库中key的数目: {}", jedis.dbSize());
        LOGGER.info("删除所有数据库中的所有key: {}", jedis.flushAll());
    }
}
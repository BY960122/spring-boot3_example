package demo.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/***
 * @author: BYDylan
 * @date: 2022/2/21
 * @description: redis string demo
 */
public class StringDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringDemo.class);
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);

    public static void main(String[] args) {
        LOGGER.info("清空数据: {}", jedis.flushDB());
        LOGGER.info("增加数据: {}", jedis.set("key1", "value1"));
        LOGGER.info("增加数据: {}", jedis.set("key2", "value2"));
        LOGGER.info("增加数据: {}", jedis.set("key3", "value3"));
        LOGGER.info("删除键key2:", jedis.del("key2"));
        LOGGER.info("获取键key2:", jedis.get("key2"));
        LOGGER.info("修改key1:", jedis.set("key1", "value1Changed"));
        LOGGER.info("获取key1的值: {}", jedis.get("key1"));
        LOGGER.info("在key3后面加入值: {}", jedis.append("key3", "End"));
        LOGGER.info("key3的值: {}", jedis.get("key3"));
        LOGGER.info("增加多个键值对: {}", jedis.mset("key01", "value01", "key02", "value02", "key03", "value03"));
        LOGGER.info("获取多个键值对: {}", jedis.mget("key01", "key02", "key03"));
        LOGGER.info("获取多个键值对: {}", jedis.mget("key01", "key02", "key03", "key04"));
        LOGGER.info("删除多个键值对: {}", jedis.del("key01", "key02"));
        LOGGER.info("获取多个键值对: {}", jedis.mget("key01", "key02", "key03"));
        jedis.flushDB();
        LOGGER.info("新增键值对防止覆盖原先值: {}", jedis.setnx("key1", "value1"));
        LOGGER.info("新增键值对防止覆盖原先值: {}", jedis.setnx("key2", "value2"));
        LOGGER.info("新增键值对防止覆盖原先值: {}", jedis.setnx("key2", "value2-new"));
        LOGGER.info("key1: {}", jedis.get("key1"));
        LOGGER.info("key2: {}", jedis.get("key2"));
        LOGGER.info("新增键值对并设置有效时间: {}", jedis.setex("key3", 2, "value3"));
        LOGGER.info("key3: {}", jedis.get("key3"));
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info(jedis.get("key3"));
        LOGGER.info("获取原值,更新为新值: {}", jedis.getSet("key2", "key2GetSet"));
        LOGGER.info(jedis.get("key2"));
        LOGGER.info("获得key2的值的字串: {}", jedis.getrange("key2", 2, 4));
    }
}
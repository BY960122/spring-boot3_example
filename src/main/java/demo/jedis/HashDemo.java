package demo.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/***
 * @author: BYDylan
 * @date: 2022/2/21
 * @description: redis hash demo
 */
public class HashDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(HashDemo.class);
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("key4", "value4");
        LOGGER.info("flushDB: {}", jedis.flushDB());
        LOGGER.info("添加名称为hash(key)的hash元素: {}", jedis.hmset("hash", map));
        LOGGER.info("向名称为hash的hash中添加key为key5,value为value5元素: {}", jedis.hset("hash", "key5", "value5"));
        LOGGER.info("散列hash的所有键值对为: {}", jedis.hgetAll("hash"));
        LOGGER.info("散列hash的所有键为: {}", jedis.hkeys("hash"));
        LOGGER.info("散列hash的所有值为: {}", jedis.hvals("hash"));
        LOGGER.info("将key6保存的值加上一个整数,如果key6不存在则添加key6: {}", jedis.hincrBy("hash", "key6", 6));
        LOGGER.info("散列hash的所有键值对为: {}", jedis.hgetAll("hash"));
        LOGGER.info("将key6保存的值加上一个整数,如果key6不存在则添加key6: {}", jedis.hincrBy("hash", "key6", 3));
        LOGGER.info("散列hash的所有键值对为: {}", jedis.hgetAll("hash"));
        LOGGER.info("删除一个或者多个键值对: {}", jedis.hdel("hash", "key2"));
        LOGGER.info("散列hash的所有键值对为: {}", jedis.hgetAll("hash"));
        LOGGER.info("散列hash中键值对的个数: {}", jedis.hlen("hash"));
        LOGGER.info("判断hash中是否存在key2: {}", jedis.hexists("hash", "key2"));
        LOGGER.info("判断hash中是否存在key3: {}", jedis.hexists("hash", "key3"));
        LOGGER.info("获取hash中的值: {}", jedis.hmget("hash", "key3"));
        LOGGER.info("获取hash中的值: {}", jedis.hmget("hash", "key3", "key4"));
    }
}
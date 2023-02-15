package demo.jedis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.HashMap;

/***
 * @author: BYDylan
 * @date: 2022/2/21
 * @description: redis 事物demo
 */
public class TransactionDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionDemo.class);
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);
    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        LOGGER.info("清空数据: {}", jedis.flushDB());
        HashMap<String, String> map = new HashMap<>();
        map.put("hello", "world");
        map.put("name", "kuangshen");

        // 开启事务
        Transaction multi = jedis.multi();
        try {
            String result = mapper.writeValueAsString(map);
            jedis.watch(result);
            multi.set("user1", result);
            multi.set("user2", result);
            // 代码抛出异常事务,执行失败
            // int i = 1 / 0;
            // 执行事务
            multi.exec();
        } catch (Exception e) {
            multi.discard(); // 放弃事务
            e.printStackTrace();
        } finally {
            System.out.println(jedis.get("user1"));
            System.out.println(jedis.get("user2"));
            jedis.close();
        }
    }
}

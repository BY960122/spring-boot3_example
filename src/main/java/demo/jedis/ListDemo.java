package demo.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/***
 * @author: BYDylan
 * @date: 2022/2/21
 * @description: redis list demo
 */
public class ListDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListDemo.class);
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);

    public static void main(String[] args) {
        LOGGER.info("清空数据: {}", jedis.flushDB());
        LOGGER.info("添加一个list: {}", jedis.lpush("collections", "ArrayList", "Vector", "Stack", "HashMap", "WeakHashMap", "LinkedHashMap"));
        jedis.lpush("collections", "HashSet");
        jedis.lpush("collections", "TreeSet");
        jedis.lpush("collections", "TreeMap");
        // -1代表倒数第一个元素,-2代表倒数第二个元素,end为-1表示查询全部
        LOGGER.info("collections的内容: {}", jedis.lrange("collections", 0, -1));
        LOGGER.info("collections区间0-3的元素: {}", jedis.lrange("collections", 0, 3));
        // 删除列表指定的值,第二个参数为删除的个数（有重复时）,后add进去的值先被删,类似于出栈
        LOGGER.info("删除指定元素个数: {}", jedis.lrem("collections", 2, "HashMap"));
        LOGGER.info("collections的内容: {}", jedis.lrange("collections", 0, -1));
        LOGGER.info("删除下表0-3区间之外的元素: {}", jedis.ltrim("collections", 0, 3));
        LOGGER.info("collections的内容: {}", jedis.lrange("collections", 0, -1));
        LOGGER.info("collections列表出栈（左端）: {}", jedis.lpop("collections"));
        LOGGER.info("collections的内容: {}", jedis.lrange("collections", 0, -1));
        LOGGER.info("collections添加元素,从列表右端,与lpush相对应: {}", jedis.rpush("collections", "EnumMap"));
        LOGGER.info("collections的内容: {}", jedis.lrange("collections", 0, -1));
        LOGGER.info("collections列表出栈（右端）: {}", jedis.rpop("collections"));
        LOGGER.info("collections的内容: {}", jedis.lrange("collections", 0, -1));
        LOGGER.info("修改collections指定下标1的内容: {}", jedis.lset("collections", 1, "LinkedArrayList"));
        LOGGER.info("collections的内容: {}", jedis.lrange("collections", 0, -1));
        LOGGER.info("collections的长度: {}", jedis.llen("collections"));
        LOGGER.info("获取collections下标为2的元素: {}", jedis.lindex("collections", 2));
        jedis.lpush("sortedList", "3", "6", "2", "0", "7", "4");
        LOGGER.info("sortedList排序前: {}", jedis.lrange("sortedList", 0, -1));
        jedis.sort("sortedList");
        LOGGER.info("sortedList排序后: {}", jedis.lrange("sortedList", 0, -1));
    }
}
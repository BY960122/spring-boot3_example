package demo.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/***
 * @author: BYDylan
 * @date: 2022/2/21
 * @description: redis set demo
 */
public class SetDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetDemo.class);
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);

    public static void main(String[] args) {
        LOGGER.info("清空数据: {}", jedis.flushDB());
        LOGGER.info("向集合中添加元素(不重复): {}", jedis.sadd("eleSet", "e1", "e2", "e4", "e3", "e0", "e8", "e7", "e5"));
        LOGGER.info("向集合中添加元素(不重复): {}", jedis.sadd("eleSet", "e6"));
        LOGGER.info("向集合中添加元素(不重复): {}", jedis.sadd("eleSet", "e6"));
        LOGGER.info("eleSet的所有元素为: {}" + jedis.smembers("eleSet"));
        LOGGER.info("删除一个元素e0: {}" + jedis.srem("eleSet", "e0"));
        LOGGER.info("eleSet的所有元素为: {}" + jedis.smembers("eleSet"));
        LOGGER.info("删除两个元素e7和e6: {}" + jedis.srem("eleSet", "e7", "e6"));
        LOGGER.info("eleSet的所有元素为: {}" + jedis.smembers("eleSet"));
        LOGGER.info("随机的移除集合中的一个元素: {}" + jedis.spop("eleSet"));
        LOGGER.info("随机的移除集合中的一个元素: {}" + jedis.spop("eleSet"));
        LOGGER.info("eleSet的所有元素为: {}" + jedis.smembers("eleSet"));
        LOGGER.info("eleSet中包含元素的个数: {}" + jedis.scard("eleSet"));
        LOGGER.info("e3是否在eleSet中: {}" + jedis.sismember("eleSet", "e3"));
        LOGGER.info("e1是否在eleSet中: {}" + jedis.sismember("eleSet", "e1"));
        LOGGER.info("e1是否在eleSet中: {}" + jedis.sismember("eleSet", "e5"));
        jedis.sadd("eleSet1", "e1", "e2", "e4", "e3", "e0", "e8", "e7", "e5");
        jedis.sadd("eleSet2", "e1", "e2", "e4", "e3", "e0", "e8");
        LOGGER.info("将eleSet1中删除e1并存入eleSet3中: {}" + jedis.smove("eleSet1", "eleSet3", "e1"));//移到集合元素
        LOGGER.info("将eleSet1中删除e2并存入eleSet3中: {}" + jedis.smove("eleSet1", "eleSet3", "e2"));
        LOGGER.info("eleSet1中的元素: {}" + jedis.smembers("eleSet1"));
        LOGGER.info("eleSet3中的元素: {}" + jedis.smembers("eleSet3"));
        LOGGER.info("============集合运算=================");
        LOGGER.info("eleSet1中的元素: {}" + jedis.smembers("eleSet1"));
        LOGGER.info("eleSet2中的元素: {}" + jedis.smembers("eleSet2"));
        LOGGER.info("eleSet1和eleSet2的交集:" + jedis.sinter("eleSet1", "eleSet2"));
        LOGGER.info("eleSet1和eleSet2的并集:" + jedis.sunion("eleSet1", "eleSet2"));
        // eleSet1中有,eleSet2中没有
        LOGGER.info("eleSet1和eleSet2的差集:" + jedis.sdiff("eleSet1", "eleSet2"));
        // 求交集并将交集保存到dstkey的集合
        jedis.sinterstore("eleSet4", "eleSet1", "eleSet2");
        LOGGER.info("eleSet4中的元素: {}" + jedis.smembers("eleSet4"));
    }
}
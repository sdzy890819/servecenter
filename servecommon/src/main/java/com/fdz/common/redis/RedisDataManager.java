package com.fdz.common.redis;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
public class RedisDataManager {

    private RedisTemplate redisTemplate;

    private StringRedisSerializer stringRedisSerializer;

    private Jackson2JsonRedisSerializer jackson2JsonRedisSerializer;


    /**
     * 设置
     *
     * @param key
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @SuppressWarnings("unchecked")
    public void set(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean exist(String key) {
        Object object = redisTemplate.opsForValue().get(key);
        return object != null;
    }

    /**
     * 获取
     *
     * @param key
     * @return
     */
    public Object get(Object key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 批量获取
     *
     * @param keys
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> get(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 获取总数
     *
     * @param keys
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Long> hLen(List<String> keys) {
        List<byte[]> rawKeys = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            rawKeys.add(getByte(keys.get(i)));
        }
        return (List<Long>) redisTemplate.execute(connection -> {
            List<Long> tmp = new ArrayList<>();
            for (int i = 0; i < rawKeys.size(); i++) {
                tmp.add(connection.hLen(rawKeys.get(i)));
            }
            return tmp;
        }, true);

    }

    /**
     * 删除key对应的存储内容。
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置 并设定过期时间。默认单位为：秒
     *
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     */
    @SuppressWarnings("unchecked")
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        if (timeout > 0) {
            if (timeUnit != null) {
                redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            } else {
                redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * 并发锁
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean setNX(final String key, final Object value) {
        return (Boolean) redisTemplate.execute((RedisConnection redisConnection) ->
                redisConnection.setNX(redisTemplate.getKeySerializer().serialize(key), redisTemplate.getKeySerializer().serialize(value))
        );
    }

    /**
     * 设置Map
     *
     * @param key
     * @param map
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setHashMap(String key, Map map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 设置有效期。timeout和timeUnit 设置尽量超过1秒
     *
     * @param key
     * @param map
     * @param timeout
     * @param timeUnit
     * @param <K>
     * @param <V>
     */
    @SuppressWarnings("unchecked")
    public <K, V> void setHashMap(String key, Map<K, V> map, long timeout, TimeUnit timeUnit) {
        final Map<byte[], byte[]> hashes = new LinkedHashMap<>(map.size());
        final byte[] keyByte = getByte(key);

        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            hashes.put(getByte(entry.getKey()), getByte(entry.getValue()));
        }
        redisTemplate.execute((RedisConnection connection) -> {
            connection.hMSet(keyByte, hashes);
            connection.expire(keyByte, timeUnit.toSeconds(timeout));
            return null;
        });
    }

    public <T> byte[] getByte(T object) {
        if (object instanceof String) {
            return stringRedisSerializer.serialize((String) object);
        }
        return jackson2JsonRedisSerializer.serialize(object);

    }

    @SuppressWarnings("unchecked")
    public <K> K byteToString(byte[] bytes) {
        return (K) stringRedisSerializer.deserialize(bytes);
    }

    @SuppressWarnings("unchecked")
    public <K> K byteToObject(byte[] bytes) {
        return (K) jackson2JsonRedisSerializer.deserialize(bytes);
    }

    /**
     * 获取map内的key是否存在.
     *
     * @param key
     * @param mapKey
     */
    @SuppressWarnings("unchecked")
    public Boolean hasKey(String key, String mapKey) {
        return redisTemplate.opsForHash().hasKey(key, mapKey);
    }


    /**
     * Get key set (fields) of hash at {@code key}.
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<String> hkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }


    /**
     * Get value for given {@code hashKey} from hash at {@code key}.
     *
     * @param key
     * @param hashKey
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object hget(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 根据key， 类型，获取key对应的整个Map
     *
     * @param key
     * @param hkClass
     * @param hvClass
     * @param <K>
     * @param <V>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> entries(String key, Class<?> hkClass, Class<?> hvClass) {
        final byte[] rawKey = getByte(key);

        Map<byte[], byte[]> entries = (Map<byte[], byte[]>) redisTemplate.execute(connection ->
                connection.hGetAll(rawKey), true);
        Map<K, V> map = new LinkedHashMap<>(entries.size());
        for (Map.Entry<byte[], byte[]> entry : entries.entrySet()) {
            try {
                map.put((hkClass.newInstance() instanceof String ? byteToString(entry.getKey()) : byteToObject(entry.getKey())),
                        (hvClass.newInstance() instanceof String ? byteToString(entry.getValue()) : byteToObject(entry.getValue())));
            } catch (InstantiationException e) {
                log.error("InstantiationException", e);
            } catch (IllegalAccessException e) {
                log.error("IllegalAccessException", e);
            }
        }
        return map;
    }

    /**
     * 根据KEY获取对应的MAP
     *
     * @param key
     * @return
     */
    public Map<String, Object> entries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * Set the {@code value} of a hash {@code hashKey}.
     *
     * @param key
     * @param hashKey
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void hset(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * Delete given hash {@code hashKeys}.
     *
     * @param key
     * @param hashKeys
     * @return
     */
    @SuppressWarnings("unchecked")
    public Long hdel(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * increment {@code value} of a hash {@code hashKey} by the given {@code delta}.
     *
     * @param key
     * @param hashKey
     * @param delta
     * @return
     */
    @SuppressWarnings("unchecked")
    public Long increment(String key, String hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 过期时间.
     *
     * @param key
     * @param timeout
     * @param timeUnit
     */
    @SuppressWarnings("unchecked")
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    @SuppressWarnings("unchecked")
    public <T> long expire(List<T> key, final long timeout, final TimeUnit unit) {
        final long rawTimeout = TimeoutUtils.toMillis(timeout, unit);
        List<byte[]> bytes = new ArrayList<>();
        for (int i = 0; i < key.size(); i++) {
            bytes.add(getByte(key.get(i)));
        }
        return (Long) redisTemplate.execute(connection -> {
            Long p = 0L;
            try {
                for (int i = 0; i < bytes.size(); i++) {
                    boolean bool = connection.pExpire(bytes.get(i), rawTimeout);
                    if (bool) {
                        p++;
                    }
                }
            } catch (Exception e) {
                log.error("expire connection exception", e);
            }
            return p;
        }, true);
    }

    /**
     * 按照时间设置过期 根据unix时间计算
     *
     * @param key
     * @param date
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }


    /**
     * 按照给定的时间形式。返回@param key 的过期时间， 如果不指定timeunit
     * 则按照秒的形式返回。
     *
     * @param key
     * @param timeUnit
     * @return
     */
    @SuppressWarnings("unchecked")
    public Long getExpire(String key, TimeUnit timeUnit) {
        if (timeUnit != null) {
            return redisTemplate.getExpire(key, timeUnit);
        }
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 按照TTL命令返回结果作为返回
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * Zset形式。返回zset数组。按照开始和结束下标获取列表。end=-1的时候获取的是完整的
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set zrange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * Zset形式。从高到低返回zset数组，按照开始和结束下标获取列表。end=-1的时候获取完成列表。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set reverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取Zset的数量
     *
     * @param key
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public Long zsize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 往Zset内添加元素及排序字段
     *
     * @param key
     * @param value
     * @param number
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public boolean zadd(String key, Object value, double number) {
        return redisTemplate.opsForZSet().add(key, value, number);
    }

    @SuppressWarnings({"unchecked"})
    public <V> Long zadd(String key, Set<ZSetOperations.TypedTuple<V>> set) {
        return redisTemplate.opsForZSet().add(key, set);
    }

    /**
     * 按照评分查询评分区间内的列表元素
     *
     * @param key
     * @param number
     * @param number2
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set zrangeByScore(String key, double number, double number2) {
        return redisTemplate.opsForZSet().rangeByScore(key, number, number2);
    }

    /**
     * Diff all sets for given {@code key} and {@code otherKey}.
     *
     * @param key      must not be {@literal null}.
     * @param otherKey must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sdiff">Redis Documentation: SDIFF</a>
     */
    @SuppressWarnings({"unchecked"})
    public <V> Set<V> sdiff(String key, String otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }


    /**
     * Add given {@code values} to set at {@code key}.
     *
     * @param key    must not be {@literal null}.
     * @param values
     * @return
     * @see <a href="http://redis.io/commands/sadd">Redis Documentation: SADD</a>
     */
    @SuppressWarnings({"unchecked"})
    public Long sadd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @SuppressWarnings({"unchecked"})
    public <V> Set<V> members(String key) {
        return redisTemplate.opsForSet().members(key);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public List randomList(String key, int num) {
        return redisTemplate.opsForSet().randomMembers(key, num);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set distinctRandomSet(String key, int num) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, num);
    }

    /**
     * 加有效期的原子锁
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public boolean lock(final String key, final String value, final long expire, final TimeUnit timeUnit) {

        return (Boolean) redisTemplate.execute((RedisConnection redisConnection) ->
                redisConnection.setNX(redisTemplate.getKeySerializer().serialize(key), redisTemplate.getKeySerializer().serialize(value))
                        && redisTemplate.expire(key, expire, timeUnit != null ? timeUnit : TimeUnit.SECONDS)
        );

    }
}

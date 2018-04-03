package com.atis.util;

import com.google.inject.Inject;
import net.csdn.ServiceFramwork;
import net.csdn.common.exception.SettingsException;
import net.csdn.common.settings.Settings;
import net.csdn.modules.cache.*;
import net.csdn.modules.compress.gzip.GZip;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by xshd000 on 2017/6/16.
 */
public class RedisClient {
    private JedisPool pool;

    @Inject
    public RedisClient(Settings settings) {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
//            config.setMaxActive(300);
//            config.setMaxIdle(200);
//            config.setMinIdle(10);
            config.setTestOnBorrow(true);
            this.pool = new JedisPool(config,
                    settings.get(ServiceFramwork.mode + ".datasources.redis.host") == null?"127.0.0.1":settings.get(ServiceFramwork.mode + ".datasources.redis.host"),
                    settings.getAsInt(ServiceFramwork.mode + ".datasources.redis.port", Integer.valueOf(6379)).intValue(),
                    settings.getAsInt(ServiceFramwork.mode + ".datasources.redis.timeout", Integer.valueOf(3000)).intValue(),
                    settings.get(ServiceFramwork.mode + ".datasources.redis.password"),
                    settings.getAsInt(ServiceFramwork.mode + ".datasources.redis.database",
                            Integer.valueOf(0)).intValue());
        } catch (SettingsException var3) {

        }

    }

    public void operate(net.csdn.modules.cache.RedisClient.Callback callback) {
        Jedis jedis = this.borrow();

        try {
            callback.execute(jedis);
        } finally {
            this.revert(jedis);
        }

    }

    public String get(String key) {
        Jedis jedis = this.borrow();

        String var3;
        try {
            var3 = jedis.get(key);
        } finally {
            this.revert(jedis);
        }

        return var3;
    }

    public String bGet(String key) {
        Jedis jedis = this.borrow();

        String var4;
        try {
            byte[] value = jedis.get(key.getBytes());
            if(value == null) {
                var4 = null;
                return var4;
            }

            var4 = GZip.decodeWithGZip(value);
        } finally {
            this.revert(jedis);
        }

        return var4;
    }

    public String set(String key, String value) {
        Jedis jedis = this.borrow();

        String var4;
        try {
            var4 = jedis.set(key, value);
        } finally {
            this.revert(jedis);
        }

        return var4;
    }

    public void del(String key) {
        Jedis jedis = this.borrow();

        try {
            jedis.del(key);
        } finally {
            this.revert(jedis);
        }

    }

    public String bSet(String key, String value) {
        Jedis jedis = this.borrow();

        String var4;
        try {
            var4 = jedis.set(key.getBytes(), GZip.encodeWithGZip(value));
        } finally {
            this.revert(jedis);
        }

        return var4;
    }

    public boolean exits(String key) {
        Jedis jedis = this.borrow();

        boolean var3;
        try {
            var3 = jedis.exists(key).booleanValue();
        } finally {
            this.revert(jedis);
        }

        return var3;
    }

    public List<String> mget(String[] keys) {
        Jedis jedis = this.borrow();

        List var3;
        try {
            var3 = jedis.mget(keys);
        } finally {
            this.revert(jedis);
        }

        return var3;
    }

    public String info() {
        Jedis jedis = this.borrow();

        String var2;
        try {
            var2 = jedis.info();
        } finally {
            this.revert(jedis);
        }

        return var2;
    }

    public List<String> bMget(String[] keys) {
        Jedis jedis = this.borrow();
        int len = keys.length;
        byte[][] bkeys = new byte[len][];

        for(int list = 0; list < keys.length; ++list) {
            bkeys[list] = keys[list].getBytes();
        }

        try {
            List var12 = jedis.mget(bkeys);
            ArrayList temp_list = new ArrayList(var12.size());
            Iterator var7 = var12.iterator();

            while(var7.hasNext()) {
                byte[] temp = (byte[])var7.next();
                temp_list.add(GZip.decodeWithGZip(temp));
            }

            ArrayList var13 = temp_list;
            return var13;
        } finally {
            this.revert(jedis);
        }
    }

    public Set<String> sCopy(String key, String new_key) {
        Jedis jedis = this.borrow();

        try {
            Set oldSets = jedis.smembers(key);
            Iterator var5 = oldSets.iterator();

            while(var5.hasNext()) {
                String str = (String)var5.next();
                jedis.sadd(new_key, new String[]{str});
            }

            Set var10 = oldSets;
            return var10;
        } finally {
            this.revert(jedis);
        }
    }

    public void sClear(String key, String oldKey) {
        Jedis jedis = this.borrow();

        try {
            Set oldSets = jedis.smembers(key);
            Iterator var5 = oldSets.iterator();

            while(var5.hasNext()) {
                String str = (String)var5.next();
                jedis.del(oldKey + ":" + str);
            }

            jedis.del(key);
        } finally {
            this.revert(jedis);
        }
    }

    public Set<String> sMove(String key, String new_key) {
        Jedis jedis = this.borrow();

        try {
            Set oldSets = jedis.smembers(key);
            Iterator var5 = oldSets.iterator();

            while(var5.hasNext()) {
                String str = (String)var5.next();
                jedis.smove(key, new_key, str);
            }

            Set var10 = oldSets;
            return var10;
        } finally {
            this.revert(jedis);
        }
    }

    public void destory() {
        this.pool.destroy();
    }

    public synchronized Jedis borrow() {
        return (Jedis)this.pool.getResource();
    }

    public void revert(Jedis jedis) {
        this.pool.returnResource(jedis);
    }

    public String setObject(Object object, String key) {
        Jedis jedis = this.borrow();

        String var4;
        try {
            var4 = jedis.set(key.getBytes(), net.csdn.modules.cache.SerializeUtil.serialize(object));
        } finally {
            this.revert(jedis);
        }

        return var4;
    }

    public Object getObject(String key) {
        Jedis jedis = this.borrow();

        Object var4;
        try {
            byte[] value = jedis.get(key.getBytes());
            var4 = net.csdn.modules.cache.SerializeUtil.unserialize(value);
        } finally {
            this.revert(jedis);
        }

        return var4;
    }

    public Long zadd(String key, double score, String member) {
        Jedis jedis = this.borrow();

        Long var6;
        try {
            var6 = jedis.zadd(key, score, member);
        } finally {
            this.revert(jedis);
        }

        return var6;
    }

    public Long zcard(String key) {
        Jedis jedis = this.borrow();

        Long var3;
        try {
            var3 = jedis.zcard(key);
        } finally {
            this.revert(jedis);
        }

        return var3;
    }

    public Set<String> zrevrange(String key, long start, long end) {
        Jedis jedis = this.borrow();

        Set var7;
        try {
            var7 = jedis.zrevrange(key, start, end);
        } finally {
            this.revert(jedis);
        }

        return var7;
    }

    public Set<String> sinter(String... keys) {
        Jedis jedis = this.borrow();

        Set var3;
        try {
            var3 = jedis.sinter(keys);
        } finally {
            this.revert(jedis);
        }

        return var3;
    }

    public Long incr(String key) {
        Jedis jedis = this.borrow();

        Long var3;
        try {
            var3 = jedis.incr(key);
        } finally {
            this.revert(jedis);
        }

        return var3;
    }

    public Double zscore(String key, String member) {
        Jedis jedis = this.borrow();

        Double var4;
        try {
            var4 = jedis.zscore(key, member);
        } finally {
            this.revert(jedis);
        }

        return var4;
    }

    public Long zrem(String key, String... member) {
        Jedis jedis = this.borrow();

        Long var4;
        try {
            var4 = jedis.zrem(key, member);
        } finally {
            this.revert(jedis);
        }

        return var4;
    }

    public String set(String key, Object object) {
        Jedis jedis = this.borrow();
        return jedis.set(key.getBytes(), net.csdn.modules.cache.SerializeUtil.serialize(object));
    }

    public Object getObj(String key) {
        Jedis jedis = this.borrow();

        Object var4;
        try {
            byte[] value = jedis.get(key.getBytes());
            var4 = net.csdn.modules.cache.SerializeUtil.unserialize(value);
        } finally {
            this.revert(jedis);
        }

        return var4;
    }

    public interface Callback {
        void execute(Jedis var1);
    }
}

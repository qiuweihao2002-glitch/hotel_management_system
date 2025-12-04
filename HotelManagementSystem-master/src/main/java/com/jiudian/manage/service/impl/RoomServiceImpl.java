package com.jiudian.manage.service.impl;

import com.github.pagehelper.PageHelper;
import com.jiudian.manage.mapper.RoomMapper;
import com.jiudian.manage.model.Room;
import com.jiudian.manage.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomMapper roomMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;  // 操作 Redis 字符串

    @Autowired
    private ObjectMapper objectMapper; // Spring Boot 默认会有这个 Bean

    private static final String ROOM_LIST_CACHE_KEY_PREFIX = "room:list:";

    private static final String ROOM_LOCK_KEY_PREFIX = "lock:room:";

    private void unlockSafe(String key, String value) {
        String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "   return redis.call('del', KEYS[1]) " +
                        "else " +
                        "   return 0 " +
                        "end";
        stringRedisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                value
        );
    }


    private String buildRoomListKey(int state, int type, int pageNum, int pageSize) {
        return ROOM_LIST_CACHE_KEY_PREFIX
                + "state=" + state
                + ":type=" + type
                + ":page=" + pageNum
                + ":size=" + pageSize;
    }

    @Override
    public boolean addRoom(String local, double money, int state, int type) {
        Room room = new Room();
        room.setLocal(local);
        room.setMoney(money);
        room.setState(state);
        room.setType(type);
        int i = roomMapper.insertSelective(room);
        if (i > 0) {
            clearRoomListCache(); // 新增房间后，清理列表缓存
            return true;
        }
        return false;
    }


    public void clearRoomListCache() {
        // 使用 SCAN 而不是 KEYS，避免阻塞 Redis
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {

            Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions()
                            .match(ROOM_LIST_CACHE_KEY_PREFIX + "*")  // 匹配 room:list:* 的所有 key
                            .count(100)                                // 每批最多返回 100 个 key
                            .build()
            );

            try {
                while (cursor.hasNext()) {
                    byte[] key = cursor.next();
                    connection.del(key); // 逐个删除
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
            return null;
        });
    }



    @Override
    public boolean delRoom(int roomid) {
        String lockKey = ROOM_LOCK_KEY_PREFIX + roomid;
        String lockVal = UUID.randomUUID().toString();

        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockVal);
        if (success == null || !success) {
            // 有人正在操作这间房，直接删除失败
            System.out.println("[RoomService] 房间 " + roomid + " 正在操作中，此次操作失败，请稍后再试");
            return false;
        }
        // 单独设置过期时间（你这版 Spring Data Redis 没有 4 个参数的重载）
        stringRedisTemplate.expire(lockKey, 30, TimeUnit.SECONDS);

        try {
            int i = roomMapper.deleteByPrimaryKey(roomid);
            System.out.println("[RoomService] 房间 " + roomid + " 正在执行删除操作中，此次操作成功！");
            if (i > 0) {
                clearRoomListCache();
                return true;
            }
            return false;
        } finally {
            unlockSafe(lockKey, lockVal);
        }
    }

    @Override
    public boolean updateRoom(int roomid, String local, double money, int state, int type) {
        String lockKey = ROOM_LOCK_KEY_PREFIX + roomid;
        String lockVal = UUID.randomUUID().toString();

        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockVal);
        if (success == null || !success) {
            return false;
        }
        stringRedisTemplate.expire(lockKey, 30, TimeUnit.SECONDS);

        try {
            Room room = new Room();
            room.setRoomid(roomid);
            if (!"null".equals(local)) {
                room.setLocal(local);
            }
            if (money != -1) {
                room.setMoney(money);
            }
            if (state != -1) {
                room.setState(state);
            }
            if (type != -1) {
                room.setType(type);
            }
            int i = roomMapper.updateByPrimaryKeySelective(room);
            if (i > 0) {
                clearRoomListCache();
                return true;
            }
            return false;
        } finally {
            unlockSafe(lockKey, lockVal);
        }
    }

    @Override
    public boolean updateRoomState(int roomid, int state) {
        String lockKey = ROOM_LOCK_KEY_PREFIX + roomid;
        String lockVal = UUID.randomUUID().toString();

        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockVal);
        if (success == null || !success) {
            return false;
        }
        stringRedisTemplate.expire(lockKey, 30, TimeUnit.SECONDS);

        try {
            Room room = new Room();
            room.setRoomid(roomid);
            room.setState(state);
            int i = roomMapper.updateByPrimaryKeySelective(room);
            if (i > 0) {
                clearRoomListCache();
                return true;
            }
            return false;
        } finally {
            unlockSafe(lockKey, lockVal);
        }
    }

    @Override
    public List<Room> getRoomByState(int state, int type, int pageNum, int pageSize) {
        // 1. 先拼接缓存 key
        String cacheKey = buildRoomListKey(state, type, pageNum, pageSize);

        try {
            // 2. 先从 Redis 里查
            String cachedJson = stringRedisTemplate.opsForValue().get(cacheKey);
            if (StringUtils.hasText(cachedJson)) {
                // 命中缓存，反序列化成 List<Room> 直接返回
                List<Room> cachedList = objectMapper.readValue(
                        cachedJson,
                        new TypeReference<List<Room>>() {}
                );
                System.out.println("[RoomService] 命中 Redis 缓存：" + cacheKey);
                return cachedList;
            }
        } catch (Exception e) {
            // 缓存出问题时不要影响主流程，简单打个日志
            e.printStackTrace();
        }

        // 3. 没命中缓存，走数据库
        Room room = new Room();
        if (state != -1) {
            room.setState(state);
        }
        if (type != -1) {
            room.setType(type);
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Room> dbList = roomMapper.selectRoomByStateType(room);

        // 4. 把查询结果写入缓存，设置一个过期时间（例如 5 分钟）
        try {
            String json = objectMapper.writeValueAsString(dbList);
            stringRedisTemplate
                    .opsForValue()
                    .set(cacheKey, json, 5, TimeUnit.MINUTES);
            System.out.println("[RoomService] 写入 Redis 缓存：" + cacheKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbList;
    }


    @Override
    public Room getRoomById(int roomid) {
        return roomMapper.selectByPrimaryKey(roomid);
    }
}

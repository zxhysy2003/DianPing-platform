package com.hmdp.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result getTypeList() {
        String key = "cache:type:list";
        List<String> list = stringRedisTemplate.opsForList().range(key, 0, -1);

        if (list != null && list.size() > 0) {
            JSONArray jsonArray = JSONUtil.parseArray(list.toString());
            return Result.ok(JSONUtil.toList(jsonArray,ShopType.class));
        }

        List<ShopType> typeList = query().orderByAsc("sort").list();

        if (typeList == null) {
            return Result.fail("发生错误");
        }

        for (ShopType shopType : typeList) {
            String jsonStr = JSONUtil.toJsonStr(shopType);
            stringRedisTemplate.opsForList().rightPush(key,jsonStr);
        }
        return Result.ok(typeList);
    }
}

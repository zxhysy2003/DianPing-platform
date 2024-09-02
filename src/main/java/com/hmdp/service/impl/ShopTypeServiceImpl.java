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
import java.util.stream.Collectors;

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
        String key = "shop:type:list";
        List<String> list = stringRedisTemplate.opsForList().range(key, 0, -1);

        if (list != null && list.size() > 0) {
            List<ShopType> res = list.stream()
                    .map(str -> JSONUtil.toBean(str,ShopType.class))
                    .collect(Collectors.toList());
            return Result.ok(res);
        }

        List<ShopType> res = query().orderByAsc("sort").list();

        res.forEach(shopType -> stringRedisTemplate.opsForList()
                .rightPush(key, JSONUtil.toJsonStr(shopType)));
        return Result.ok(res);
    }
}

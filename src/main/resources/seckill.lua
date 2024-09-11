---
--- Created by S.Y.
--- DateTime: 9/10/2024 9:59 PM
---

-- 1.参数列表
-- 1.1 优惠券id
local voucherId = ARGV[1]
-- 1.2 用户id
local userId = ARGV[2]

-- 2.数据key
-- 2.1 库存key
local stockKey = 'seckill:stock:' .. voucherId
local orderKey = 'seckill:order:' .. voucherId

-- 3.脚本业务
-- 3.1 判断库存是否充足 get stockKey
if(tonumber(redis.call('get',stockKey)) <= 0) then
    -- 库存不足，返回1
    return 1
end
-- 3.2 判断用户是否下单 SISMEMBER orderKey userId
if(redis.call('sismember', orderKey, userId) == 1) then
    -- 存在，说明是重复下单，返回2
    return 2
end
-- 3.3 扣库存 incrby stockKey -1
redis.call('incrby', stockKey, -1)
-- 3.4 下单(保存用户) sadd orderKey userId
redis.call('sadd', orderKey, userId)

return 0

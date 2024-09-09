-- 比较线程标识是否与锁中的标识一致
if (redis.call('GET',KEYS[1]) == ARGV[1]) then
    -- 释放锁 del key
    return redis.call('DEL',KEYS[1])
end
return 0
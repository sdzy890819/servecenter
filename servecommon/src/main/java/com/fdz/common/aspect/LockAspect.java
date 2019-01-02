package com.fdz.common.aspect;

import com.fdz.common.aspect.ann.Lock;
import com.fdz.common.constant.Constants;
import com.fdz.common.exception.BizException;
import com.fdz.common.redis.RedisDataManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 锁机制实现类，通过aspect来横切方法，来执行注入式锁.
 *
 * @author zhangyang
 */
@Component
@Aspect
public class LockAspect extends BaseAspect {

    @Resource
    private RedisDataManager redisDataManager;

    @Pointcut("@annotation(com.fdz.common.aspect.ann.Lock)")
    public void lockPoint() {

    }

    @Around(value = "lockPoint() && @annotation(lock)")
    public Object lockAroud(ProceedingJoinPoint pjp, Lock lock) throws Throwable {
        return doLock(pjp, lock);
    }

    public Object doLock(ProceedingJoinPoint pjp, Lock lock) throws Throwable {
        Object result = null;
        String key = Constants.RedisKey.LOCK_PREFIX.concat(parse(lock.key(), getMethod(pjp), pjp.getArgs()));
        if (redisDataManager.lock(key, key, lock.time(), lock.unit())) {
            try {
                result = pjp.proceed();
            } catch (BizException e) {
                throw e;
            } catch (Exception e) {
                throw e;
            } finally {
                if (lock.unlock()) {
                    redisDataManager.delete(key);
                }
            }
        } else {
            throw lock.exception().throwException();
        }
        return result;
    }

}

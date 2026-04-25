package com.example.optimization_algorithm_backend.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long currentUserId = resolveCurrentUserId();
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "createdBy", Long.class, currentUserId);
        this.strictInsertFill(metaObject, "updatedBy", Long.class, currentUserId);
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updatedBy", Long.class, resolveCurrentUserId());
    }

    private Long resolveCurrentUserId() {
        try {
            if (StpUtil.isLogin()) {
                return Long.valueOf(StpUtil.getLoginIdAsString());
            }
        } catch (Exception ignored) {
            return 0L;
        }
        return 0L;
    }
}

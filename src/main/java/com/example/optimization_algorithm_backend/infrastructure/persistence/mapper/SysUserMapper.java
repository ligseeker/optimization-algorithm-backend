package com.example.optimization_algorithm_backend.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
// 作用：标记这是一个MyBatis的Mapper接口，MyBatis会自动为这个接口生成实现类，并将其注册到Spring容器中，使得我们可以通过依赖注入的方式使用它来进行数据库操作。通过使用@Mapper注解，可以简化配置，提升开发效率，同时也增强了代码的可读性和维护性。
public interface SysUserMapper extends BaseMapper<SysUserEntity> {
}

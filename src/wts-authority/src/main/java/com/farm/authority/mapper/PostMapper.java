package com.farm.authority.mapper;

import com.farm.authority.domain.Post;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PostMapper {
    int deleteEntity(String id);

    int insertEntity(Post record);

    int insertSelective(Post record);

    Post getEntity(String id);

    int editEntity(Post record);

    int updateByPrimaryKey(Post record);

    List<Post> findByOrganizationid(String organizationid);
}
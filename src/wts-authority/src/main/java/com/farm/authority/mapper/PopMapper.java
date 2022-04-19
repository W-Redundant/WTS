package com.farm.authority.mapper;

import com.farm.authority.domain.Pop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PopMapper {
    int deleteEntity(String id);

    int deleteEntityByTargetidAndOid(@Param("TARGETID") String targetid,@Param("OID") String oid,@Param("TARGETTYPE") String targettype);

    int insertEntity(Pop record);

    int insertSelective(Pop record);

    Pop getEntity(String id);

    List<Pop> findBytargetId(String targetId);

    int updateByPrimaryKeySelective(Pop record);

    int updateByPrimaryKey(Pop record);

}
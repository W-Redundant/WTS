package com.wts.exam.mapper;

import com.wts.exam.domain.PaperUserown;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PaperUserownMapper {
    int deleteEntity(String id);

    int insertEntity(PaperUserown record);

    int insertSelective(PaperUserown record);

    PaperUserown getEntity(String id);

    List<PaperUserown> findByCardId(String cardID);

    int editEntity(PaperUserown record);

    int updateByPrimaryKey(PaperUserown record);

    int deleteByCardId(String cardId);

    int deleteByRoomId(String roomId);

    int countByCuserAndModelType(@Param("cuser") String cuser, @Param("modelType") String modelType);

    int countByModelTypeAndPaperId(@Param("modelType") String modelType, @Param("paperId") String paperId);

    int countByModelTypeAndPaperIdAndCuser(@Param("modelType") String modelType, @Param("paperId") String paperId,@Param("cuser") String cuser);


    int deleteByPaperIdAndModerTypeAndCuser(@Param("paperId") String paperId, @Param("modelType") String modelType, @Param("cuser") String cuser);


}
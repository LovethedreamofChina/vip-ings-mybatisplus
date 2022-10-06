package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.TablesMapper;
import com.example.demo.utils.BaseQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author ljr
 * @date 2022-09-27 15:52
 */
@Mapper
public interface BaseTableMapper extends BaseMapper<Map> {

    TablesMapper t = new TablesMapper();

    @Select("<script>" +
            "select ${ew.paramNameValuePairs.cr} from ${ew.paramNameValuePairs.table} " +
            "${ew.customSqlSegment} " +
            "<if test='ew.customSqlSegment == null or ew.customSqlSegment == \"\" and ew.paramNameValuePairs.eqTable != null and ew.paramNameValuePairs.eqTable != \"\"'>" +
            "where " +
            "</if>" +
            "${ew.paramNameValuePairs.eqTable} ${ew.paramNameValuePairs.by}" +
            "</script>")
    Map<String, Object> getObj(@Param("ew") BaseQueryWrapper queryWrapper);


    @Select("<script>" +
            "select ${ew.paramNameValuePairs.cr} from ${ew.paramNameValuePairs.table} " +
            "${ew.customSqlSegment} " +
            "<if test='ew.customSqlSegment == null or ew.customSqlSegment == \"\" and ew.paramNameValuePairs.eqTable != null and ew.paramNameValuePairs.eqTable != \"\"'>" +
            "where " +
            "</if>" +
            "${ew.paramNameValuePairs.eqTable} ${ew.paramNameValuePairs.by}" +
            "</script>")
    List<Map<String, Object>> getObjList(@Param("ew") BaseQueryWrapper queryWrapper);
}

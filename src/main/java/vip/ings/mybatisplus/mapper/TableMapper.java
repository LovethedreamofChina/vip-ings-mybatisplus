package vip.ings.mybatisplus.mapper;

import vip.ings.mybatisplus.config.TablesMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ljr
 * @date 2022-10-10 14:38
 */
@Mapper
public interface TableMapper extends BaseTableMapper {
    TablesMapper t = (TablesMapper) BaseTableMapper.getT(TablesMapper.class);
}

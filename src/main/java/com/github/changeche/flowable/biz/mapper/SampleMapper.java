package com.github.changeche.flowable.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.changeche.flowable.biz.entity.Sample;
import com.github.changeche.flowable.common.annotation.DBType;
import com.github.changeche.flowable.model.enums.DBTypeEnum;

/**
 * <p>
 * Sample
 * </p>
 *
 * @author Chenjing
 */
@DBType(DBTypeEnum.BIZ)
public interface SampleMapper extends BaseMapper<Sample> {

}

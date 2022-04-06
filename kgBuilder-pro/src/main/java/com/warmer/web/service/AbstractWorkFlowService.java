package com.warmer.web.service;

import com.warmer.web.domain.DataNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 流程组件父类
 *
 * @author tanc
 * @time 2022-1-21 10:03:59
 */
@Configuration
@Data
@Slf4j
public abstract class AbstractWorkFlowService {
    /**
     * 传入参数为CpNode的id
     * 组建逻辑之执行成功返回true, 执行失败返回false
     *
     * @param dataNode 当前组件
     * @return 是否处理成功
     */
    public abstract boolean process(DataNode dataNode);

}

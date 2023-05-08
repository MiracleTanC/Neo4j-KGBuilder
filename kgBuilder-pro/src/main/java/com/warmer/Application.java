package com.warmer;

import cn.hutool.core.util.IdUtil;
import com.warmer.base.util.DateUtil;
import com.warmer.base.util.Neo4jUtil;
import com.warmer.web.entity.KgDomain;
import com.warmer.web.service.KGGraphService;
import com.warmer.web.service.KGManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@ComponentScan(basePackages = {"com.warmer"})
public class Application implements ApplicationRunner {

    @Autowired
    private KGManagerService kgManagerService;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 启动时初始化现有的neo4j 标签到mysql
     * 将标签同步到mysql管理是为了权限区分，根据需要选择，mysql不是必须
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, Object> labelsInfo = Neo4jUtil.getLabelsInfo();
        if(labelsInfo!=null&&labelsInfo.keySet().size()>0){
            for (String label : labelsInfo.keySet()) {
                long value=(Long) labelsInfo.get(label);
                KgDomain domainModel = kgManagerService.getDomainByLabel(label);
                if(domainModel!=null){
                    domainModel.setModifyTime(DateUtil.getDateNow());
                    domainModel.setModifyUser("sa");
                    domainModel.setNodeCount(value);
                    kgManagerService.updateDomain(domainModel);
                }else {
                    domainModel=KgDomain.builder()
                            .label(label)
                            .name(label)
                            .nodeCount(value)
                            .createTime(DateUtil.getDateNow())
                            .shipCount(0)
                            .type(0)
                            .status(1)
                            .commend(0)
                            .build();
                    kgManagerService.saveDomain(domainModel);
                }
            }
        }
    }
}

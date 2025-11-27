package com.warmer;

import com.warmer.base.util.DateUtil;
import com.warmer.base.util.Neo4jUtil;
import com.warmer.web.entity.KgDomain;
import com.warmer.web.service.KGManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * 应用入口
 *
 * - 启动 Spring Boot 应用
 * - 可选：启动后从 Neo4j 读取标签并同步到 MySQL 领域表
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.warmer"})
public class Application implements ApplicationRunner {

    @Autowired
    private KGManagerService kgManagerService;
    @Value("${app.initNeo4jOnStartup:true}")
    private boolean initNeo4jOnStartup;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 应用启动后回调
     *
     * 从 Neo4j 读取现有标签及节点计数，并根据配置同步到 MySQL 的领域表。
     *
     * @param args 启动参数
     * @throws Exception 执行期间的异常
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!initNeo4jOnStartup){
            return;
        }
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

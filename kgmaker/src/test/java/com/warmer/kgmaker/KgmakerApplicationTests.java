package com.warmer.kgmaker;

import com.alibaba.fastjson.JSON;
import com.warmer.kgmaker.util.Neo4jUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes = KgmakerApplication.class) // 指定我们SpringBoot工程的Application启动类
@WebAppConfiguration // 由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
public class KgmakerApplicationTests {

	@Autowired
	private Neo4jUtil neo4jUtil;
	@Test
	public void contextLoads() {
		String cyphersql="MATCH (n:`贵州`) WHERE n.name='交通事件' " + 
				"CALL apoc.path.spanningTree(n, {maxLevel:3}) YIELD path" + 
				" RETURN path";
		HashMap<String, Object> result=neo4jUtil.GetGraphNodeAndShip(cyphersql);
		System.out.println(JSON.toJSON(result));
	}

}

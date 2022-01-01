package com.warmer.base.util;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.driver.v1.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

@Component
@Lazy(false)
public class Neo4jUtil {

	private static Driver neo4jDriver;

	private static Logger log =  LoggerFactory.getLogger(Neo4jUtil.class);

	@Autowired
	@Lazy
	public void setNeo4jDriver(Driver neo4jDriver) {
		Neo4jUtil.neo4jDriver = neo4jDriver;
	}

	/**
	 * 测试neo4j连接是否打开
	 *
	 * @return
	 */
	public static boolean isNeo4jOpen() {
		try (Session session = neo4jDriver.session()) {
			log.debug("连接成功：" + session.isOpen());
			return session.isOpen();
		} catch (Exception e) {

		}
		return false;
	}

	/**
	 * neo4j驱动执行cypher
	 *
	 * @param cypherSql
	 * @return
	 */
	public static StatementResult executeCypherSql(String cypherSql) {
		StatementResult result = null;
		try (Session session = neo4jDriver.session()) {
			log.debug(cypherSql);
			result = session.run(cypherSql);
		}
		return result;
	}
	public static void runCypherSql(String cypherSql) {
		try (Session session = neo4jDriver.session()) {
			log.debug(cypherSql);
			session.run(cypherSql);
		}
	}
	/**
	 * 返回节点集合，此方法不保留关系
	 *
	 * @param cypherSql
	 * @return
	 */
	public static List<HashMap<String, Object>> getGraphNode(String cypherSql) {
		List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
		try {
			StatementResult result = executeCypherSql(cypherSql);
			if (result.hasNext()) {
				List<Record> records = result.list();
				for (Record recordItem : records) {
					List<Pair<String, Value>> f = recordItem.fields();
					for (Pair<String, Value> pair : f) {
						HashMap<String, Object> rss = new HashMap<String, Object>();
						String typeName = pair.value().type().name();
						if (typeName.equals("NODE")) {
							Node noe4jNode = pair.value().asNode();
							String uuid = String.valueOf(noe4jNode.id());
							Map<String, Object> map = noe4jNode.asMap();
							for (Entry<String, Object> entry : map.entrySet()) {
								String key = entry.getKey();
								rss.put(key, entry.getValue());
							}
							rss.put("uuid", uuid);
							ents.add(rss);
						}
					}

				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return ents;
	}

	/**
	 * 返回关系，不保留节点内容
	 *
	 * @param cypherSql
	 * @return
	 */
	public static List<HashMap<String, Object>> getGraphRelationShip(String cypherSql) {
		List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
		try {
			StatementResult result = executeCypherSql(cypherSql);
			if (result.hasNext()) {
				List<Record> records = result.list();
				for (Record recordItem : records) {
					List<Pair<String, Value>> f = recordItem.fields();
					for (Pair<String, Value> pair : f) {
						HashMap<String, Object> rss = new HashMap<String, Object>();
						String typeName = pair.value().type().name();
						if (typeName.equals("RELATIONSHIP")) {
							Relationship rship = pair.value().asRelationship();
							String uuid = String.valueOf(rship.id());
							String sourceId = String.valueOf(rship.startNodeId());
							String targetId = String.valueOf(rship.endNodeId());
							Map<String, Object> map = rship.asMap();
							for (Entry<String, Object> entry : map.entrySet()) {
								String key = entry.getKey();
								rss.put(key, entry.getValue());
							}
							rss.put("uuid", uuid);
							rss.put("sourceId", sourceId);
							rss.put("targetId", targetId);
							ents.add(rss);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return ents;
	}


	/**
	 * 获取值类型的结果,如count,uuid
	 *
	 * @return 1 2 3 等数字类型
	 */
	public static long getGraphValue(String cypherSql) {
		long val = 0;
		try {
			StatementResult cypherResult = executeCypherSql(cypherSql);
			if (cypherResult.hasNext()) {
				Record record = cypherResult.next();
				for (Value value : record.values()) {
					val = value.asLong();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return val;
	}

	/**
	 * 返回节点和关系，节点node,关系relationship,路径path,集合list,map
	 *
	 * @param cypherSql
	 * @return
	 */
	public static HashMap<String, Object> getGraphNodeAndShip(String cypherSql) {
		HashMap<String, Object> mo = new HashMap<String, Object>();
		try {
			StatementResult result = executeCypherSql(cypherSql);
			if (result.hasNext()) {
				List<Record> records = result.list();
				List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
				List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
				List<String> uuids = new ArrayList<String>();
				for (Record recordItem : records) {
					List<Pair<String, Value>> f = recordItem.fields();
					for (Pair<String, Value> pair : f) {
						HashMap<String, Object> rShips = new HashMap<String, Object>();
						HashMap<String, Object> rss = new HashMap<String, Object>();
						String typeName = pair.value().type().name();
						if ("NULL".equals(typeName)) {
							continue;
						}
						if ("NODE".equals(typeName)) {
							Node noe4jNode = pair.value().asNode();
							Map<String, Object> map = noe4jNode.asMap();
							String uuid = String.valueOf(noe4jNode.id());
							if (!uuids.contains(uuid)) {
								for (Entry<String, Object> entry : map.entrySet()) {
									String key = entry.getKey();
									rss.put(key, entry.getValue());
								}
								rss.put("uuid", uuid);
								uuids.add(uuid);
							}
							if (!rss.isEmpty()) {
								ents.add(rss);
							}
						} else if ("RELATIONSHIP".equals(typeName)) {
							Relationship rship = pair.value().asRelationship();
							String uuid = String.valueOf(rship.id());
							String sourceId = String.valueOf(rship.startNodeId());
							String targetId = String.valueOf(rship.endNodeId());
							Map<String, Object> map = rship.asMap();
							for (Entry<String, Object> entry : map.entrySet()) {
								String key = entry.getKey();
								rShips.put(key, entry.getValue());
							}
							rShips.put("uuid", uuid);
							rShips.put("sourceId", sourceId);
							rShips.put("targetId", targetId);
							if (!rShips.isEmpty()) {
								ships.add(rShips);
							}
						} else if ("PATH".equals(typeName)) {
							Path path = pair.value().asPath();
							for (Node nodeItem : path.nodes()) {
								Map<String, Object> map = nodeItem.asMap();
								String uuid = String.valueOf(nodeItem.id());
								rss = new HashMap<String, Object>();
								if (!uuids.contains(uuid)) {
									for (Entry<String, Object> entry : map.entrySet()) {
										String key = entry.getKey();
										rss.put(key, entry.getValue());
									}
									rss.put("uuid", uuid);
									uuids.add(uuid);
								}
								if (!rss.isEmpty()) {
									ents.add(rss);
								}
							}
							for (Relationship next : path.relationships()) {
								rShips = new HashMap<String, Object>();
								String uuid = String.valueOf(next.id());
								String sourceId = String.valueOf(next.startNodeId());
								String targetId = String.valueOf(next.endNodeId());
								Map<String, Object> map = next.asMap();
								for (Entry<String, Object> entry : map.entrySet()) {
									String key = entry.getKey();
									rShips.put(key, entry.getValue());
								}
								rShips.put("uuid", uuid);
								rShips.put("sourceId", sourceId);
								rShips.put("targetId", targetId);
								if (!rShips.isEmpty()) {
									ships.add(rShips);
								}
							}
						} else if (typeName.contains("LIST")) {
							Iterable<Value> val = pair.value().values();
							Value next = val.iterator().next();
							String type = next.type().name();
							if ("RELATIONSHIP".equals(type)) {
								Relationship rship = next.asRelationship();
								String uuid = String.valueOf(rship.id());
								String sourceId = String.valueOf(rship.startNodeId());
								String targetId = String.valueOf(rship.endNodeId());
								Map<String, Object> map = rship.asMap();
								for (Entry<String, Object> entry : map.entrySet()) {
									String key = entry.getKey();
									rShips.put(key, entry.getValue());
								}
								rShips.put("uuid", uuid);
								rShips.put("sourceId", sourceId);
								rShips.put("targetId", targetId);
								if (!rShips.isEmpty()) {
									ships.add(rShips);
								}
							}
						} else if (typeName.contains("MAP")) {
							rss.put(pair.key(), pair.value().asMap());
						} else {
							rss.put(pair.key(), pair.value().toString());
							if (!rss.isEmpty()) {
								ents.add(rss);
							}
						}
					}
				}
				mo.put("node", ents);
				mo.put("relationship", toDistinctList(ships));
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return mo;
	}


	/**
	 * 去掉json键的引号，否则neo4j会报错
	 *
	 * @param jsonStr
	 * @return
	 */
	public static String getFilterPropertiesJson(String jsonStr) {
		return jsonStr.replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2"); // 去掉key的引号
	}

	/**
	 * 对象转json，key=value,用于 cypher set语句
	 *
	 * @param obj
	 * @param <T>
	 * @return
	 */
	public static <T> String getKeyValCyphersql(T obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> sqlList = new ArrayList<String>();
		// 得到类对象
		Class userCla = obj.getClass();
		/* 得到类中的所有属性集合 */
		Field[] fs = userCla.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			Class type = f.getType();

			f.setAccessible(true); // 设置些属性是可以访问的
			Object val = new Object();
			try {
				val = f.get(obj);
				if (val == null) {
					val = "";
				}
				String sql = "";
				String key = f.getName();
				if (val instanceof String[]) {
					//如果为true则强转成String数组
					String[] arr = (String[]) val;
					String v = "";
					for (int j = 0; j < arr.length; j++) {
						arr[j] = "'" + arr[j] + "'";
					}
					v = String.join(",", arr);
					sql = "n." + key + "=[" + val + "]";
				} else if (val instanceof List) {
					//如果为true则强转成String数组
					List<String> arr = (ArrayList<String>) val;
					List<String> aa = new ArrayList<String>();
					String v = "";
					for (String s : arr) {
						s = "'" + s + "'";
						aa.add(s);
					}
					v = String.join(",", aa);
					sql = "n." + key + "=[" + v + "]";
				} else {
					// 得到此属性的值
					map.put(key, val);// 设置键值
					if (type.getName().equals("int")) {
						sql = "n." + key + "=" + val + "";
					} else {
						sql = "n." + key + "='" + val + "'";
					}
				}

				sqlList.add(sql);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error(e.getMessage());
			}
		}
		return  String.join(",", sqlList);
	}

	/**
	 * 将haspmap集合反序列化成对象集合
	 *
	 * @param maps
	 * @param type
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> hashMapToObject(List<HashMap<String, Object>> maps, Class<T> type) {
		try {
			List<T> list = new ArrayList<T>();
			for (HashMap<String, Object> r : maps) {
				T t = type.newInstance();
				Iterator iter = r.entrySet().iterator();// 该方法获取列名.获取一系列字段名称.例如name,age...
				while (iter.hasNext()) {
					Entry entry = (Entry) iter.next();// 把hashmap转成Iterator再迭代到entry
					String key = entry.getKey().toString(); // 从iterator遍历获取key
					Object value = entry.getValue(); // 从hashmap遍历获取value
					if ("serialVersionUID".toLowerCase().equals(key.toLowerCase())) {
						continue;
					}
					Field field = type.getDeclaredField(key);// 获取field对象
					if (field != null) {
						//System.out.print(field.getType());
						field.setAccessible(true);
						//System.out.print(field.getType().getName());
						if (field.getType() == int.class || field.getType() == Integer.class) {
							if (value == null || StringUtil.isBlank(value.toString())) {
								field.set(t, 0);// 设置值
							} else {
								field.set(t, Integer.parseInt(value.toString()));// 设置值
							}
						} else if (field.getType() == long.class || field.getType() == Long.class) {
							if (value == null || StringUtil.isBlank(value.toString())) {
								field.set(t, 0);// 设置值
							} else {
								field.set(t, Long.parseLong(value.toString()));// 设置值
							}

						} else if (field.getType() == Double.class) {
							if (value == null || StringUtil.isBlank(value.toString())) {
								field.set(t, 0.0);// 设置值
							} else {
								field.set(t, Double.parseDouble(value.toString()));// 设置值
							}

						} else {
							if (field.getType().equals(List.class)) {
								if (value == null || StringUtil.isBlank(value.toString())) {
									field.set(t, null);
								} else {
									field.set(t, value);// 设置值
								}
							} else {
								field.set(t, value);// 设置值
							}
						}
					}

				}
				list.add(t);
			}

			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将haspmap反序列化成对象
	 *
	 * @param map
	 * @param type
	 * @param <T>
	 * @return
	 */
	public static <T> T hashMapToObjectItem(HashMap<String, Object> map, Class<T> type) {
		try {
			T t = type.newInstance();
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();// 把hashmap转成Iterator再迭代到entry
				String key = entry.getKey().toString(); // 从iterator遍历获取key
				Object value = entry.getValue(); // 从hashmap遍历获取value
				if ("serialVersionUID".toLowerCase().equals(key.toLowerCase())) {
					continue;
				}
				Field field = type.getDeclaredField(key);// 获取field对象
				if (field != null) {
					field.setAccessible(true);
					if (field.getType() == int.class || field.getType() == Integer.class) {
						if (value == null || StringUtil.isBlank(value.toString())) {
							field.set(t, 0);// 设置值
						} else {
							field.set(t, Integer.parseInt(value.toString()));// 设置值
						}
					} else if (field.getType() == long.class || field.getType() == Long.class) {
						if (value == null || StringUtil.isBlank(value.toString())) {
							field.set(t, 0);// 设置值
						} else {
							field.set(t, Long.parseLong(value.toString()));// 设置值
						}

					} else if (field.getType() == Double.class) {
						if (value == null || StringUtil.isBlank(value.toString())) {
							field.set(t, 0.0);// 设置值
						} else {
							field.set(t, Double.parseDouble(value.toString()));// 设置值
						}

					} else {
						if (field.getType().equals(List.class)) {
							if (value == null || StringUtil.isBlank(value.toString())) {
								field.set(t, null);
							} else {
								field.set(t, value);// 设置值
							}
						} else {
							field.set(t, value);// 设置值
						}

					}
				}

			}

			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 返回单个节点信息
	 */
	public static HashMap<String, Object> getOneNode(String cypherSql) {
		HashMap<String, Object> ret = new HashMap<String, Object>();
		try {
			StatementResult result = executeCypherSql(cypherSql);
			if (result.hasNext()) {
				Record record = result.list().get(0);
				Pair<String, Value> f = record.fields().get(0);
				String typeName = f.value().type().name();
				if ("NODE".equals(typeName)) {
					Node noe4jNode = f.value().asNode();
					String uuid = String.valueOf(noe4jNode.id());
					Map<String, Object> map = noe4jNode.asMap();
					for (Entry<String, Object> entry : map.entrySet()) {
						String key = entry.getKey();
						ret.put(key, entry.getValue());
					}
					ret.put("uuid", uuid);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return ret;
	}

	public static boolean batchRunCypherWithTx(List<String> cyphers) {
		Session session = neo4jDriver.session();
		try (Transaction tx = session.beginTransaction()) {
			for (String cypher : cyphers) {
				tx.run(cypher);
			}
			tx.success();
		} catch (Exception e) {
			log.info(e.getMessage());
			return false;
		}
		return true;
	}


	public static List<HashMap<String, Object>> toDistinctList(List<HashMap<String, Object>> list) {
		Set<String> keysSet = new HashSet<String>();
		Iterator<HashMap<String, Object>> it = list.iterator();
		while (it.hasNext()) {
			HashMap<String, Object> map = it.next();
			String uuid = (String) map.get("uuid");
			int beforeSize = keysSet.size();
			keysSet.add(uuid);
			int afterSize = keysSet.size();
			if (afterSize != (beforeSize + 1)) {
				it.remove();
			}
		}
		return list;
	}
}

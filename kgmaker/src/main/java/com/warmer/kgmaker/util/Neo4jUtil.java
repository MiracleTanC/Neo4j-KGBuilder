package com.warmer.kgmaker.util;

import com.alibaba.fastjson.JSON;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.driver.v1.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

@Component
public class Neo4jUtil {
	@Autowired
	private Driver neo4jDriver;

	public boolean isNeo4jOpen() {
		try (Session session = neo4jDriver.session()) {
			System.out.println("连接成功：" + session.isOpen());
			return session.isOpen();
		} catch (Exception e) {

		}
		return false;
	}

	public StatementResult excuteCypherSql(String cypherSql) {
		StatementResult result = null;
		try (Session session = neo4jDriver.session()) {
			System.out.println(cypherSql);
			result = session.run(cypherSql);
			session.close();
		} catch (Exception e) {
			throw e;
		}
		return result;
	}


	public HashMap<String, Object> GetEntityMap(String cypherSql) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			StatementResult result = excuteCypherSql(cypherSql);
			if (result.hasNext()) {
				List<Record> records = result.list();
				for (Record recordItem : records) {
					for (Value value : recordItem.values()) {
						if (value.type().name().equals("NODE")) {// 结果里面只要类型为节点的值
							Node noe4jNode = value.asNode();
							Map<String, Object> map = noe4jNode.asMap();
							for (Entry<String, Object> entry : map.entrySet()) {
								String key = entry.getKey();
								if (rss.containsKey(key)) {
									String oldValue = rss.get(key).toString();
									String newValue = oldValue + "," + entry.getValue();
									rss.replace(key, newValue);
								} else {
									rss.put(key, entry.getValue());
								}
							}

						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rss;
	}

	public List<HashMap<String, Object>> GetGraphNode(String cypherSql) {
		List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
		try {
			StatementResult result = excuteCypherSql(cypherSql);
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
			e.printStackTrace();
		}
		return ents;
	}

	public List<HashMap<String, Object>> GetGraphRelationShip(String cypherSql) {
		List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
		try {
			StatementResult result = excuteCypherSql(cypherSql);
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
							String sourceid = String.valueOf(rship.startNodeId());
							String targetid = String.valueOf(rship.endNodeId());
							Map<String, Object> map = rship.asMap();
							for (Entry<String, Object> entry : map.entrySet()) {
								String key = entry.getKey();
								rss.put(key, entry.getValue());
							}
							rss.put("uuid", uuid);
							rss.put("sourceid", sourceid);
							rss.put("targetid", targetid);
							ents.add(rss);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ents;
	}
	public List<HashMap<String, Object>> GetGraphItem(String cypherSql) {
		List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
		List<String> nodeids = new ArrayList<String>();
		List<String> shipids = new ArrayList<String>();
		try {
			StatementResult result = excuteCypherSql(cypherSql);
			if (result.hasNext()) {
				List<Record> records = result.list();
				for (Record recordItem : records) {
					List<Pair<String, Value>> f = recordItem.fields();
					HashMap<String, Object> rss = new HashMap<String, Object>();
					for (Pair<String, Value> pair : f) {
						String typeName = pair.value().type().name();
						if (typeName.equals("NODE")) {
							Node noe4jNode = pair.value().asNode();
							String uuid = String.valueOf(noe4jNode.id());
							if(!nodeids.contains(uuid)) {
								Map<String, Object> map = noe4jNode.asMap();
								for (Entry<String, Object> entry : map.entrySet()) {
									String key = entry.getKey();
									rss.put(key, entry.getValue());
								}
								rss.put("uuid", uuid);
							}
						}else if (typeName.equals("RELATIONSHIP")) {
							Relationship rship = pair.value().asRelationship();
							String uuid = String.valueOf(rship.id());
							if (!shipids.contains(uuid)) {
								String sourceid = String.valueOf(rship.startNodeId());
								String targetid = String.valueOf(rship.endNodeId());
								Map<String, Object> map = rship.asMap();
								for (Entry<String, Object> entry : map.entrySet()) {
									String key = entry.getKey();
									rss.put(key, entry.getValue());
								}
								rss.put("uuid", uuid);
								rss.put("sourceid", sourceid);
								rss.put("targetid", targetid);
							}
						}else {
							rss.put(pair.key(),pair.value().toString());
						}
					}
					ents.add(rss);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ents;
	}
	/*
	 * 获取值类型的结果,如count,uuid
	 * @return 1 2 3 等数字类型
	 */
	public long GetGraphValue(String cypherSql) {
		long val=0;
		try {
			StatementResult cypherResult = excuteCypherSql(cypherSql);
			if (cypherResult.hasNext()) {
				Record record = cypherResult.next();
				for (Value value : record.values()) {
					val = value.asLong();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	public HashMap<String, Object> GetGraphNodeAndShip(String cypherSql) {
		HashMap<String, Object> mo = new HashMap<String, Object>();
		try {
			StatementResult result = excuteCypherSql(cypherSql);
			if (result.hasNext()) {
				List<Record> records = result.list();
				List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
				List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
				List<String> uuids = new ArrayList<String>();
				List<String> shipids = new ArrayList<String>();
				for (Record recordItem : records) {
					List<Pair<String, Value>> f = recordItem.fields();
					for (Pair<String, Value> pair : f) {
						HashMap<String, Object> rships = new HashMap<String, Object>();
						HashMap<String, Object> rss = new HashMap<String, Object>();
						String typeName = pair.value().type().name();
						if (typeName.equals("NULL")) {
							continue;
						} else if (typeName.equals("NODE")) {
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
                            if (rss != null && !rss.isEmpty()) {
                                ents.add(rss);
                            }
						} else if (typeName.equals("RELATIONSHIP")) {
							Relationship rship = pair.value().asRelationship();
                            String uuid = String.valueOf(rship.id());
                            if (!shipids.contains(uuid)) {
                                String sourceid = String.valueOf(rship.startNodeId());
                                String targetid = String.valueOf(rship.endNodeId());
                                Map<String, Object> map = rship.asMap();
                                for (Entry<String, Object> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    rships.put(key, entry.getValue());
                                }
                                rships.put("uuid", uuid);
                                rships.put("sourceid", sourceid);
                                rships.put("targetid", targetid);
                                if (rships != null && !rships.isEmpty()) {
                                    ships.add(rships);
                                }
                            }

						} else if (typeName.equals("PATH")) {
							Path path = pair.value().asPath();
							Map<String, Object> startNodemap = path.start().asMap();
							String startNodeuuid = String.valueOf(path.start().id());
							if (!uuids.contains(startNodeuuid)) {
								rss=new HashMap<String, Object>();
								for (Entry<String, Object> entry : startNodemap.entrySet()) {
									String key = entry.getKey();
									rss.put(key, entry.getValue());
								}
								rss.put("uuid", startNodeuuid);
								uuids.add(startNodeuuid);
								if (rss != null && !rss.isEmpty()) {
									ents.add(rss);
								}
							}

							Map<String, Object> endNodemap = path.end().asMap();
							String endNodeuuid = String.valueOf(path.end().id());
							if (!uuids.contains(endNodeuuid)) {
								rss=new HashMap<String, Object>();
								for (Entry<String, Object> entry : endNodemap.entrySet()) {
									String key = entry.getKey();
									rss.put(key, entry.getValue());
								}
								rss.put("uuid", endNodeuuid);
								uuids.add(endNodeuuid);
								if (rss != null && !rss.isEmpty()) {
									ents.add(rss);
								}
							}
							Iterator<Node> allNodes = path.nodes().iterator();
							while (allNodes.hasNext()) {
								Node next = allNodes.next();
								String uuid = String.valueOf(next.id());
								if (!uuids.contains(uuid)) {
									rss=new HashMap<String, Object>();
									Map<String, Object> map = next.asMap();
									for (Entry<String, Object> entry : map.entrySet()) {
										String key = entry.getKey();
										rss.put(key, entry.getValue());
									}
									rss.put("uuid", uuid);
									uuids.add(uuid);
									if (rss != null && !rss.isEmpty()) {
										ents.add(rss);
									}
								}
							}
							Iterator<Relationship> reships = path.relationships().iterator();
							while (reships.hasNext()) {
								Relationship next = reships.next();
								String uuid = String.valueOf(next.id());
								if (!shipids.contains(uuid)) {
									rships=new HashMap<String, Object>();
									String sourceid = String.valueOf(next.startNodeId());
									String targetid = String.valueOf(next.endNodeId());
									Map<String, Object> map = next.asMap();
									for (Entry<String, Object> entry : map.entrySet()) {
										String key = entry.getKey();
										rships.put(key, entry.getValue());
									}
									rships.put("uuid", uuid);
									rships.put("sourceid", sourceid);
									rships.put("targetid", targetid);
									if (rships != null && !rships.isEmpty()) {
										ships.add(rships);
									}
								}
							}
						} else if (typeName.contains("LIST")) {
							Iterable<Value> val=pair.value().values();
                            Value next = val.iterator().next();
                            String type=next.type().name();
                            if (type.equals("RELATIONSHIP")) {
                                Relationship rship = next.asRelationship();
                                String uuid = String.valueOf(rship.id());
                                if (!shipids.contains(uuid)) {
                                    String sourceid = String.valueOf(rship.startNodeId());
                                    String targetid = String.valueOf(rship.endNodeId());
                                    Map<String, Object> map = rship.asMap();
                                    for (Entry<String, Object> entry : map.entrySet()) {
                                        String key = entry.getKey();
                                        rships.put(key, entry.getValue());
                                    }
                                    rships.put("uuid", uuid);
                                    rships.put("sourceid", sourceid);
                                    rships.put("targetid", targetid);
                                    if (rships != null && !rships.isEmpty()) {
                                        ships.add(rships);
                                    }
                                }
                            }
						} else if (typeName.contains("MAP")) {
							rss.put(pair.key(), pair.value().asMap());
						} else {
							rss.put(pair.key(), pair.value().toString());
                            if (rss != null && !rss.isEmpty()) {
                                ents.add(rss);
                            }
						}
						
					}
				}
				mo.put("node", ents);
				mo.put("relationship", ships);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return mo;
	}
	/**
	 * 匹配所有类型的节点,可以是节点,关系,数值,路径
	 * @param cypherSql
	 * @return
	 */
	public List<HashMap<String, Object>> GetEntityList(String cypherSql) {
		List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
		try {
			StatementResult result = excuteCypherSql(cypherSql);
			if (result.hasNext()) {
				List<Record> records = result.list();
				for (Record recordItem : records) {
					HashMap<String, Object> rss = new HashMap<String, Object>();
					List<Pair<String, Value>> f = recordItem.fields();
					for (Pair<String, Value> pair : f) {
						String typeName = pair.value().type().name();
						if (typeName.equals("NULL")) {
							continue;
						} else if (typeName.equals("NODE")) {
							Node noe4jNode = pair.value().asNode();
							Map<String, Object> map = noe4jNode.asMap();
							for (Entry<String, Object> entry : map.entrySet()) {
								String key = entry.getKey();
								rss.put(key, entry.getValue());
							}
						} else if (typeName.equals("RELATIONSHIP")) {
							Relationship rship = pair.value().asRelationship();
							Map<String, Object> map = rship.asMap();
							for (Entry<String, Object> entry : map.entrySet()) {
								String key = entry.getKey();
								rss.put(key, entry.getValue());
							}
						} else if (typeName.equals("PATH")) {

						} else if (typeName.contains("LIST")) {
							rss.put(pair.key(), pair.value().asList());
						} else if (typeName.contains("MAP")) {
							rss.put(pair.key(), pair.value().asMap());
						} else {
							rss.put(pair.key(), pair.value().toString());
						}
					}
					ents.add(rss);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ents;
	}

	public <T> List<T> GetEntityItemList(String cypherSql, Class<T> type) {
		List<HashMap<String, Object>> ents=GetGraphNode(cypherSql);
		List<T> model = HashMapToObject(ents, type);
		return model;
	}

	public <T> T GetEntityItem(String cypherSql, Class<T> type) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			StatementResult result = excuteCypherSql(cypherSql);
			if (result.hasNext()) {
				Record record = result.next();
				for (Value value : record.values()) {
					if (value.type().name().equals("NODE")) {// 结果里面只要类型为节点的值
						Node noe4jNode = value.asNode();
						Map<String, Object> map = noe4jNode.asMap();
						for (Entry<String, Object> entry : map.entrySet()) {
							String key = entry.getKey();
							if (rss.containsKey(key)) {
								String oldValue = rss.get(key).toString();
								String newValue = oldValue + "," + entry.getValue();
								rss.replace(key, newValue);
							} else {
								rss.put(key, entry.getValue());
							}
						}

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		T model = HashMapToObjectItem(rss, type);
		return model;
	}

	public HashMap<String, Object> GetEntity(String cypherSql) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			StatementResult result = excuteCypherSql(cypherSql);
			if (result.hasNext()) {
				Record record = result.next();
				for (Value value : record.values()) {
					String t = value.type().name();
					if (value.type().name().equals("NODE")) {// 结果里面只要类型为节点的值
						Node noe4jNode = value.asNode();
						Map<String, Object> map = noe4jNode.asMap();
						for (Entry<String, Object> entry : map.entrySet()) {
							String key = entry.getKey();
							if (rss.containsKey(key)) {
								String oldValue = rss.get(key).toString();
								String newValue = oldValue + "," + entry.getValue();
								rss.replace(key, newValue);
							} else {
								rss.put(key, entry.getValue());
							}
						}

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rss;
	}

	public Integer executeScalar(String cypherSql) {
		Integer count = 0;
		try {
			StatementResult result = excuteCypherSql(cypherSql);
			if (result.hasNext()) {
				Record record = result.next();
				for (Value value : record.values()) {
					String t = value.type().name();
					if (t.equals("INTEGER")) {
						count = Integer.valueOf(value.toString());
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	public HashMap<String, Object> GetRelevantEntity(String cypherSql) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			StatementResult resultNode = excuteCypherSql(cypherSql);
			if (resultNode.hasNext()) {
				List<Record> records = resultNode.list();
				for (Record recordItem : records) {
					Map<String, Object> r = recordItem.asMap();
					System.out.println(JSON.toJSONString(r));
					String key = r.get("key").toString();
					if (rss.containsKey(key)) {
						String oldValue = rss.get(key).toString();
						String newValue = oldValue + "," + r.get("value");
						rss.replace(key, newValue);
					} else {
						rss.put(key, r.get("value"));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rss;
	}



	public String getFilterPropertiesJson(String jsonStr) {
		String propertiesString = jsonStr.replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2"); // 去掉key的引号
		return propertiesString;
	}
	public <T>String getkeyvalCyphersql(T obj) {
		 Map<String, Object> map = new HashMap<String, Object>();
		 List<String> sqlList=new ArrayList<String>();
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
	                if(val==null) {
	                	val="";
	                }
	                String sql="";
	                String key=f.getName();
	                System.out.println("key:"+key+"type:"+type);
	                if ( val instanceof   Integer ){
	                	// 得到此属性的值
		                map.put(key, val);// 设置键值
		                sql="n."+key+"="+val;
	    			}
	                else if ( val instanceof   String[] ){
	    				//如果为true则强转成String数组
	    				String [] arr = ( String[] ) val ;
	    				String v="";
	    				for ( int j = 0 ; j < arr.length ; j++ ){
	    					arr[j]="'"+ arr[j]+"'";
	    				}
	    				v=String.join(",", arr);
	    				sql="n."+key+"=["+val+"]";
	    			}
	                else if (val instanceof List){
	    				//如果为true则强转成String数组
	                	List<String> arr = ( ArrayList<String> ) val ;
	                	List<String> aa=new ArrayList<String>();
	    				String v="";
	    				for (String s : arr) {
	    					s="'"+ s+"'";
	    					aa.add(s);
						}
	    				v=String.join(",", aa);
	    				sql="n."+key+"=["+v+"]";
	    			}
	                else {
	                	// 得到此属性的值
		                map.put(key, val);// 设置键值
		                sql="n."+key+"='"+val+"'";
	                }
	                
	                sqlList.add(sql);
	            } catch (IllegalArgumentException e) {
	                e.printStackTrace();
	            } catch (IllegalAccessException e) {
	                e.printStackTrace();
	            }
	        }
	        String finasql=String.join(",",sqlList);
	        System.out.println("单个对象的所有键值==反射==" + map.toString());
		return finasql;
	}
	public <T> List<T> HashMapToObject(List<HashMap<String, Object>> maps, Class<T> type) {
		try {
			List<T> list = new ArrayList<T>();
			for (HashMap<String, Object> r : maps) {
				T t = type.newInstance();
				Iterator iter = r.entrySet().iterator();// 该方法获取列名.获取一系列字段名称.例如name,age...
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();// 把hashmap转成Iterator再迭代到entry
					String key = entry.getKey().toString(); // 从iterator遍历获取key
					Object value = entry.getValue(); // 从hashmap遍历获取value
					if("serialVersionUID".toLowerCase().equals(key.toLowerCase()))continue;
					Field field = type.getDeclaredField(key);// 获取field对象
					if (field != null) {
						field.setAccessible(true);
						if (field.getType() == int.class || field.getType() == Integer.class) {
							if (value==null||StringUtil.isBlank(value.toString())) {
								field.set(t, 0);// 设置值
							} else {
								field.set(t, Integer.parseInt(value.toString()));// 设置值
							}
						} 
						 else if (field.getType() == long.class||field.getType() == Long.class ) {
								if (value==null||StringUtil.isBlank(value.toString())) {
									field.set(t, 0);// 设置值
								} else {
									field.set(t, Long.parseLong(value.toString()));// 设置值
								}

						}
						 else {
							field.set(t, value);// 设置值
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

	public <T> T HashMapToObjectItem(HashMap<String, Object> map, Class<T> type) {
		try {
			T t = type.newInstance();
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();// 把hashmap转成Iterator再迭代到entry
				String key = entry.getKey().toString(); // 从iterator遍历获取key
				Object value = entry.getValue(); // 从hashmap遍历获取value
				if("serialVersionUID".toLowerCase().equals(key.toLowerCase()))continue;
				Field field = type.getDeclaredField(key);// 获取field对象
				if (field != null) {
					field.setAccessible(true);
					if (field.getType() == int.class || field.getType() == Integer.class) {
						if (value==null||StringUtil.isBlank(value.toString())) {
							field.set(t, 0);// 设置值
						} else {
							field.set(t, Integer.parseInt(value.toString()));// 设置值
						}
					} 
					 else if (field.getType() == long.class||field.getType() == Long.class ) {
							if (value==null||StringUtil.isBlank(value.toString())) {
								field.set(t, 0);// 设置值
							} else {
								field.set(t, Long.parseLong(value.toString()));// 设置值
							}

					}
					 else {
						field.set(t, value);// 设置值
					}
				}

			}

			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

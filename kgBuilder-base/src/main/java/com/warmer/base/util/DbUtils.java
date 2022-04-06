package com.warmer.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.warmer.base.common.FieldQueryItem;
import com.warmer.base.common.PageRecord;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DbUtils {
    private static Logger log =  LoggerFactory.getLogger(Neo4jUtil.class);

    private static HashMap<String, JdbcTemplate> urlAndJdbcTemplate = new HashMap<>();
    
    /**
     * 获取数据库字典信息（mysql8.0可用）
     */
    public static HashMap<String, ArrayList<HashMap<String, String>>> getMetaData(String dbType, String dbName, String jdbcUrl, String username, String password, String driverClassName) {
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection conn = getConnection(jdbcUrl, username, password, driverClassName);
        ArrayList<String> tableList = getTables(dbType, dbName, username, password, driverClassName, jdbcUrl);
        HashMap<String, ArrayList<HashMap<String, String>>> result = new HashMap<>();
        dbType = dbType.toLowerCase();
        try {
            //遍历表获取字段及字段类型
            for (String tableName : tableList) {
                ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                //获取表
                String sql = getTableColumnSQL(dbType, dbName, tableName);
                if (StringUtil.isNotBlank(sql)) {
                    arrayList.addAll(getColumnItemByTable(conn, tableName, dbName, dbType));
                    result.put(tableName, arrayList);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());;
        } finally {
            closeResource(rs, pst, conn);
        }
        return result;
    }

    public static HashMap<String, ArrayList<HashMap<String, String>>> getMetaData(String dbType, String dbName, String jdbcUrl, String username, String password, String driverClassName,List<String> tableList) {

        HashMap<String, ArrayList<HashMap<String, String>>> result = new HashMap<>();
        dbType = dbType.toLowerCase();
        Connection conn = getConnection(jdbcUrl, username, password, driverClassName);
        try {
            //遍历表获取字段及字段类型备注
            for (String tableName : tableList) {
                //获取表
                ArrayList<HashMap<String, String>> columnItems = getColumnItemByTable(conn, tableName, dbName,dbType);
                result.put(tableName, columnItems);
            }
        } catch (Exception e) {
            log.error(e.getMessage());;
        } finally {
            closeResource(null, null, conn);
        }
        return result;
    }

    private static String getTableColumnSQL(String dbType, String dbName, String tableName) {
        String sql = "";
        switch (dbType.toLowerCase()) {
            case "mysql":
            case "mariadb":
                sql = String.format("select column_name as FieldName, column_comment as AliaName,column_type as FieldType,IF(column_key='PRI',1,0) as IsPrimary from INFORMATION_SCHEMA.Columns where table_name='%s'  and table_schema='%s'",tableName,dbName);
                break;
            case "hive":
                sql = "select TBL_ID, TBL_NAME, COLUMNS_V2.CD_ID, COLUMNS_V2.COLUMN_NAME as FieldName, COLUMNS_V2.COMMENT as AliaName from TBLS left join SDS on TBLS.SD_ID = SDS.SD_ID left join COLUMNS_V2 on SDS.CD_ID = COLUMNS_V2.CD_ID where TBLS.TBL_NAME ='" + tableName + "'";
                break;
            case "postgresql":
                sql = "select a.attname as FieldName, d.description as AliaName from pg_class c, pg_attribute a, pg_description d where c.relname ='" + tableName + "'" + " and a.attnum>0 and a.attrelid = c.oid and d.objoid = a.attrelid and d.objsubid = a.attnum";
                break;
            case "oracle":
                sql = "select column_name as FieldName, comments as AliaName from user_col_comments where table_name= '" + tableName + "'";
                break;
            case "sqlserver":
                sql = "select a.name as table_name, b.name as FieldName, c.value as AliaName from sys.tables a inner join sys.columns b on b.object_id = a.object_id left join sys.extended_properties c on c.major_id = b.object_id and c.minor_id = b.column_id where a.name = '" + tableName + "'";
                break;
            default:
        }
        return sql;
    }


    /**
     * 获取指定url（库）的所有表
     */
    public static ArrayList<String> getTables(String dbType, String dbName, String username, String password, String driverClassName, String jdbcUrl) {
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection conn = null;
        //数据库的所有表名称
        ArrayList<String> tableList = new ArrayList<>();

        dbType = dbType.toLowerCase();
        String sql = getTableNameSQL(dbType, dbName);
        if (StringUtil.isNotBlank(sql)) {
            try {
                conn = getConnection(jdbcUrl, username, password, driverClassName);
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    tableList.add(rs.getString(1));
                }
            } catch (Exception e) {
                log.error(e.getMessage());;
            } finally {
                closeResource(rs, pst, conn);
            }
        }
        return tableList;
    }
    private static void closeResource(ResultSet rs, Statement pst, Connection conn) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (pst != null && !pst.isClosed()) {
                pst.close();
            }
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    private static String getTableNameSQL(String dbType, String dbName) {
        String sql = "";
        switch (dbType) {
            case "mysql":
            case "mariadb":
                sql = "SHOW FULL TABLES WHERE Table_type = 'BASE TABLE'";
                break;
            case "hive":
                sql = "show tables";
                break;
            case "postgresql":
                sql = "select tablename from pg_tables where schemaname='" + dbName + "'";
                break;
            case "oracle":
                sql = "select t.table_name from user_tables t";
                break;
            case "sqlserver":
                sql = "select sysobjects.name from sysobjects where xtype='U'";
                break;
            default:
        }
        return sql;
    }

    /**
     * 获取视图
     */
    public static ArrayList<String> getViews(String dbType, String dbName, String username, String password, String driverClassName, String jdbcUrl,
                                             int MaximumPoolSize) {
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection conn = null;
        ArrayList<String> viewNameList = new ArrayList<>();
        dbType = dbType.toLowerCase();
        String sql = getViewNameSQL(dbType, dbName);
        if (StringUtil.isNotBlank(sql)) {
            try {
                conn = getConnection(jdbcUrl, username, password, driverClassName);
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    viewNameList.add(rs.getString(1));
                }
            } catch (Exception e) {
                log.error(e.getMessage());;
            } finally {
                closeResource(rs, pst, conn);
            }
        }
        return viewNameList;
    }
    private static Connection getConnection(String jdbcUrl, String username, String password, String driverClassName) {
        Connection conn = null;
        try {
            String cacheKey = getCacheKey(jdbcUrl, username);
            if (urlAndJdbcTemplate.containsKey(cacheKey)) {
                conn = urlAndJdbcTemplate.get(cacheKey).getDataSource().getConnection();
            }  else {
                // 加载驱动
                Class.forName(driverClassName);
                // 获取链接
                conn = DriverManager.getConnection(jdbcUrl, username, password);
            }
        } catch (Exception e) {
            log.error(e.getMessage());;
        }
        return conn;
    }
    private static String getViewNameSQL(String dbType, String dbName) {
        String sql = "";
        switch (dbType) {
            case "mysql":
            case "mariadb":
                sql = "SHOW TABLE STATUS WHERE COMMENT='view'";
                break;
            case "postgresql":
                sql = "SELECT viewname FROM pg_views WHERE schemaname = '" + dbName + "'";
                break;
            case "oracle":
                sql = "select * from user_views";
                break;
            case "sqlserver":
                sql = "SELECT * FROM INFORMATION_SCHEMA.VIEWS";
                break;
            default:
        }
        return sql;
    }
    private static String getCacheKey(String jdbcUrl, String userName) {
        return jdbcUrl + ":" + userName;
    }
    /**
     * 获取指定表的字段信息
     */
    public static ArrayList<HashMap<String, String>> getColumnItemByTable(String username, String password, String driverClassName, String jdbcUrl,
                                                                          int MaximumPoolSize, String tableName, String dbName, String dbType) {
        Connection conn = null;
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        try {
            conn = getConnection(jdbcUrl, username, password, driverClassName);
            arrayList = getColumnItemByTable(conn,tableName,dbName,dbType);
        } catch (Exception e) {
            log.error(e.getMessage());;
        } finally {
            closeResource(null,null, conn);
        }
        return arrayList;
    }
    public static ArrayList<HashMap<String, String>> getColumnItemByTable(Connection conn, String tableName, String dbName, String dbType) {
        ResultSet rs = null;
        PreparedStatement pst = null;
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        try {
            String sql = getTableColumnSQL(dbType, dbName, tableName);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                HashMap<String, String> fieldMap = new HashMap<>();
                fieldMap.put("ColumnName", rs.getString("FieldName"));
                fieldMap.put("ColumnAlia", rs.getString("AliaName"));
                fieldMap.put("ColumnType", rs.getString("FieldType"));
                fieldMap.put("IsPrimary", rs.getString("IsPrimary"));
                arrayList.add(fieldMap);
            }
        } catch (Exception e) {
            log.error(e.getMessage());;
        } finally {
            closeResource(rs, pst, null);
        }
        return arrayList;
    }
    /**
     * 获取jdbc模板
     */
    private static synchronized JdbcTemplate getJDBCTemplate(String username, String password, String driverClassName, String jdbcUrl,
                                                             int MaximumPoolSize) {
        String cacheKey = getCacheKey(jdbcUrl, username);
        if (!urlAndJdbcTemplate.containsKey(cacheKey)) {
            HikariConfig dataSourceConfig = new HikariConfig();
            dataSourceConfig.setUsername(username);
            dataSourceConfig.setPassword(password);
            dataSourceConfig.setDriverClassName(driverClassName);
            dataSourceConfig.setJdbcUrl(jdbcUrl);
            dataSourceConfig.setMaximumPoolSize(MaximumPoolSize);
            dataSourceConfig.setMinimumIdle(1);
            DataSource ds = new HikariDataSource(dataSourceConfig);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
            urlAndJdbcTemplate.put(cacheKey, jdbcTemplate);
            return jdbcTemplate;
        } else {
            return urlAndJdbcTemplate.get(cacheKey);
        }
    }


    /**
     * 分页获取表中的数据
     */
    public static PageRecord<Map<String, Object>> getTableInfoByPage(int pageIndex, int pageSize, String dbType, String dbName, String jdbcUrl, String tableName, String username, String password, String driverClassName,
                                                                     int MaximumPoolSize, int selectType, String selectMessage) {
        // 分类创建jdbcUrl及sql
        // 创建pageBean对象
        PageRecord<Map<String, Object>> pageRecord = new PageRecord<Map<String, Object>>();
        dbType = dbType.toLowerCase();
        try {
            JdbcTemplate jdbcTemplate = getJDBCTemplate(username, password, driverClassName, jdbcUrl, MaximumPoolSize);
            String sql = getQuerySQL(selectType, selectMessage, dbType, tableName, dbName, pageIndex, pageSize, jdbcTemplate);
            if (StringUtil.isNotBlank(sql)) {
                // 赋值当前页
                List<Map<String, Object>> arrayList = new ArrayList<>();
                if (dbType.equals("hive")) {
                    List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
                    for (Map<String, Object> map : queryForList) {
                        Map<String, Object> m = new HashMap<>();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            String mapKey = entry.getKey();
                            mapKey = mapKey.substring(mapKey.lastIndexOf(".") + 1);
                            Object mapValue = entry.getValue();
                            m.put(mapKey, mapValue);
                        }
                        arrayList.add(m);
                    }

                } else {
                    arrayList = jdbcTemplate.queryForList(sql);
                }

                pageRecord.setPageIndex(pageIndex);
                // 赋值一页的项目数
                pageRecord.setPageSize(pageSize);
                // 赋值总记录数
                Object[] params = null;
                String countsql = getQueryCountSQL(tableName, selectType, selectMessage, dbType, dbName);
                Integer totalCount = jdbcTemplate.queryForObject(countsql, params, Integer.class);
                pageRecord.setTotalCount(totalCount);
                // 赋值每页项目
                pageRecord.setData(arrayList);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pageRecord;
    }
    public static PageRecord<Map<String, Object>> getTableInfoByPage(int pageIndex, int pageSize, String dbType, String dbName, String jdbcUrl, String tableName, String username, String password, String driverClassName,
                                                                     int maximumPoolSize, List<FieldQueryItem> filterItems) {
                return  getTableInfoByPage(pageIndex,pageSize,dbType,dbName,jdbcUrl,tableName,username,password,driverClassName,maximumPoolSize,filterItems,null);
    }

    public static PageRecord<Map<String, Object>> getTableInfoByPage(int pageIndex, int pageSize, String dbType, String dbName, String jdbcUrl, String tableName, String username, String password, String driverClassName,
                                                                     int MaximumPoolSize, List<FieldQueryItem> filterItems,List<String> columns) {
        // 分类创建jdbcUrl及sql
        // 创建pageBean对象
        PageRecord<Map<String, Object>> pageRecord = new PageRecord<Map<String, Object>>();
        dbType = dbType.toLowerCase();
        try {
            JdbcTemplate jdbcTemplate = getJDBCTemplate(username, password, driverClassName, jdbcUrl, MaximumPoolSize);
            String sql = getQuerySQL(filterItems, dbType, tableName, dbName, pageIndex, pageSize, jdbcTemplate,columns);
            if (StringUtil.isNotBlank(sql)) {
                // 赋值当前页
                List<Map<String, Object>> arrayList = new ArrayList<>();
                if (dbType.equals("hive")) {
                    List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
                    for (Map<String, Object> map : queryForList) {
                        Map<String, Object> m = new HashMap<>();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            String mapKey = entry.getKey();
                            mapKey = mapKey.substring(mapKey.lastIndexOf(".") + 1);
                            Object mapValue = entry.getValue();
                            m.put(mapKey, mapValue);
                        }
                        arrayList.add(m);
                    }

                } else {
                    arrayList = jdbcTemplate.queryForList(sql);
                }
                if(arrayList.size()>0){
                    List<String> headFields = new ArrayList<>(arrayList.get(0).keySet());
                    pageRecord.setHeads(headFields);
                }
                pageRecord.setPageIndex(pageIndex);
                // 赋值一页的项目数
                pageRecord.setPageSize(pageSize);
                // 赋值总记录数
                Object[] params = null;
                String countSql = getQueryCountSQL(tableName, filterItems, dbType, dbName);
                Integer totalCount = jdbcTemplate.queryForObject(countSql, params, Integer.class);
                pageRecord.setTotalCount(totalCount);
                // 赋值每页项目
                pageRecord.setData(arrayList);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pageRecord;
    }

    public static int getTableDataNum(String dbType, String dbName, String jdbcUrl, String tableName, String username, String password, String driverClassName,
                                      int MaximumPoolSize) {
        JdbcTemplate jdbcTemplate = null;
        String sql = getQueryCountSQL(tableName, 0, "", dbType, dbName);
        try {
            jdbcTemplate = getJDBCTemplate(username, password, driverClassName, jdbcUrl, MaximumPoolSize);
        } catch (Exception e) {
            log.error(e.getMessage());;
        }
        // 赋值总记录数
        Object[] params = null;
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count;
    }
    private static String getQueryCountSQL(String tableName, int selectType, String selectMessage, String dbType, String dbName) {
        String countsql = "";
        switch (dbType) {
            case "sqlserver":
                countsql = "select rows from sysindexes where indid<2 and id = object_id('" + tableName + "')";
                break;
            case "postgresql":
                countsql = "select count(1) from " + dbName + "." + tableName;
                break;
            default:
                countsql = "select count(1) from " + tableName;
                if (selectType == 1) {
                    String whereSQL = getWhereSQL(selectMessage);
                    countsql = countsql + whereSQL;
                }
        }
        return countsql;
    }
    private static String getQueryCountSQL(String tableName, List<FieldQueryItem> filterItems, String dbType, String dbName) {
        String countsql = "";
        switch (dbType) {
            case "sqlserver":
                countsql = "select rows from sysindexes where indid<2 and id = object_id('" + tableName + "')";
                break;
            case "postgresql":
                countsql = "select count(1) from " + dbName + "." + tableName;
                break;
            default:
                countsql = "select count(1) from " + tableName;
        }
        String whereSQL = buildWhereSql(dbType,filterItems);
        if(StringUtil.isNotBlank(whereSQL)){
            countsql=String.format(" %s where %s",countsql,whereSQL);
        }
        return countsql;
    }
    private static String getQuerySQL(int selectType, String selectMessage, String dbType, String tableName, String dbName, int pageIndex, int pageSize, JdbcTemplate jdbcTemplate) {
        String sql = "";
        if (selectType == 0) {
            switch (dbType) {
                case "mysql":
                case "mariadb":
                case "hive":
                    sql = "select * from " + tableName + " limit " + ((pageIndex - 1) * pageSize) + " ," + pageSize;
                    break;
                case "postgresql":
                    sql = "select * from " + dbName + "." + tableName + " limit " + pageSize + " offset " + ((pageIndex - 1) * pageSize);
                    break;
                case "oracle":
                    sql = "select t.* from (select e.*,rownum rn from " + tableName + " e) t where t.rn between "
                            + ((pageIndex - 1) * pageSize + 1) + " and " + pageSize * pageIndex;
                    break;
                case "sqlserver":
                    // 获取表中第一个字段名
                    String columnName = jdbcTemplate.queryForRowSet("select top 1 * from " + tableName).getMetaData().getColumnName(1);
                    sql = "select * from " + tableName + " order by " + columnName + " offset "
                            + ((pageIndex - 1) * pageSize) + " row fetch next " + pageSize + " row only";
                    break;
                default:
            }
        } else if (selectType == 1) {
            String whereSQL = getWhereSQL(selectMessage);
            switch (dbType) {
                case "mysql":
                case "mariadb":
                case "hive":
                    sql = "select * from " + tableName + whereSQL + " limit " + ((pageIndex - 1) * pageSize) + " ," + pageSize;
                    break;
                case "postgresql":
                    sql = "select * from " + dbName + "." + tableName + whereSQL + " limit " + pageSize + " offset " + ((pageIndex - 1) * pageSize);
                    sql = sql.replaceAll("`", "");
                    break;
                case "oracle":
                    sql = "select t.* from (select e.*,rownum rn from " + tableName + " e " + whereSQL + ") t where t.rn between "
                            + ((pageIndex - 1) * pageSize + 1) + " and " + pageSize * pageIndex;
                    break;
                case "sqlserver":
                    whereSQL = whereSQL.replaceAll("`", "");
                    // 获取表中第一个字段名
                    String columnName = jdbcTemplate.queryForRowSet("select top 1 * from " + tableName).getMetaData().getColumnName(1);
                    sql = "select * from " + tableName + whereSQL + " order by " + columnName + " offset "
                            + ((pageIndex - 1) * pageSize) + " row fetch next " + pageSize + " row only";
                    break;
                default:
            }
        } else if (selectType == 2) {
            sql = selectMessage;
        }
        return sql;
    }

    private static String getQuerySQL( List<FieldQueryItem> filterItems,String dbType, String tableName, String dbName, int pageIndex, int pageSize, JdbcTemplate jdbcTemplate) {
        return getQuerySQL(filterItems,dbType,tableName,dbName,pageIndex,pageSize,jdbcTemplate,null);
    }

    private static String getQuerySQL( List<FieldQueryItem> filterItems,String dbType, String tableName, String dbName, int pageIndex, int pageSize, JdbcTemplate jdbcTemplate,List<String> columns) {
        String sql = "";
        String whereSQL = buildWhereSql(dbType,filterItems);
        if(StringUtil.isNotBlank(whereSQL)){
            whereSQL=String.format(" where %s ",whereSQL);
        }
        String columnsStr="*";
        if(columns!=null&&columns.size()>0){
            columnsStr=String.join(",",columns);
        }
        switch (dbType) {
            case "mysql":
            case "mariadb":
            case "hive":
                sql = "select "+columnsStr+" from " + tableName + whereSQL + " limit " + ((pageIndex - 1) * pageSize) + " ," + pageSize;
                break;
            case "postgresql":
                sql = "select  "+columnsStr+" from " + dbName + "." + tableName + whereSQL + " limit " + pageSize + " offset " + ((pageIndex - 1) * pageSize);
                sql = sql.replaceAll("`", "");
                break;
            case "oracle":
                if(columns!=null&&columns.size()>0){
                    columnsStr=columns.stream().map(n->String.format("t.%s",n)).collect(Collectors.joining(","));
                }
                sql = "select "+columnsStr+" from (select e.*,rownum rn from " + tableName + " e " + whereSQL + ") t where t.rn between "
                        + ((pageIndex - 1) * pageSize + 1) + " and " + pageSize * pageIndex;
                break;
            case "sqlserver":
                whereSQL = whereSQL.replaceAll("`", "");
                // 获取表中第一个字段名
                String columnName = jdbcTemplate.queryForRowSet("select top 1 * from " + tableName).getMetaData().getColumnName(1);
                sql = "select "+columnsStr+" from " + tableName + whereSQL + " order by " + columnName + " offset "
                        + ((pageIndex - 1) * pageSize) + " row fetch next " + pageSize + " row only";
                break;
            default:
        }
        return sql;
    }
    private static String getWhereSQL(String selectMessage) {
        StringBuilder ret = new StringBuilder();
        ret.append(" where ");
        try {
            List<Map<String, String>> array = JsonHelper.parseObject(selectMessage, new TypeReference<List<Map<String, String>>>() {
            });
            if (array.size() > 0) {
                for (Map<String, String> map : array) {
                    if (map.get("relation").equals("not")) {
                        ret.append(" and " + "`" + map.get("fieldname") + "`" + " not like " + "'" + map.get("fieldval") + "'" + " ");
                    } else {
                        ret.append(map.get("relation") + " ").append("`" + map.get("fieldname") + "`" + " ").append(map.get("fieldrel") + " ").append("'" + map.get("fieldval") + "'" + " ");
                    }

                }
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());;
        }
        return ret.toString();
    }

    private static String buildWhereSql(String dbType,List<FieldQueryItem> filterItems) {
        StringBuilder builder = new StringBuilder();
        if (filterItems != null && filterItems.size() > 0) {
            int i=0;
            for (FieldQueryItem item : filterItems) {
                if (StringUtil.isBlank(item.getValue()) || StringUtil.isBlank(item.getJoinOperate()) || item.getCondition() == null) {
                    continue;
                }
                String str ="";
                switch (dbType.toLowerCase()){
                    case "mongo":
                        break;
                    default://mysql ,postgresql,sqlserver,oracle
                        if(item.getCondition()==-1){
                            str=String.format(" %s ( %s%s'%s') ", i == 0 ? "" : item.getJoinOperate(), item.getField(), item.getOperate(), item.getValue());
                        }else {
                            str=String.format(" %s ( %s%s'%s') ", i == 0 ? "" : item.getJoinOperate(), item.getField(), "like", "%"+item.getValue()+"%");
                        }
                        break;
                }
                if(StringUtil.isNotBlank(str)){
                    builder.append(str);
                }
                i++;
            }
        }
        return builder.toString();
    }

    private static List<String> parserFieldList(String sql) {
        List<String> result = null;
        try {
            sql = sql.toLowerCase();
            String field = sql.substring(7, sql.indexOf(" from "));
            String[] fieldList = field.split(",");
            List<String> fields = Arrays.stream(fieldList).map(n -> {
                if (n.contains(" as ")) {
                    n = n.substring(n.lastIndexOf("as") + 2).trim();
                }
                n = n.replace("`", "");
                return n;
            }).collect(Collectors.toList());
            result = fields;
        } catch (Exception e) {
            log.error(e.getMessage());;
        }
        return result;
    }
}

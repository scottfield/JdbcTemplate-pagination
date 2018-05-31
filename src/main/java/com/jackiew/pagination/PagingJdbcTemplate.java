package com.jackiew.pagination;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

/**
 * Created by Administrator on 2016/8/29.
 */
public class PagingJdbcTemplate extends JdbcTemplate {
    public PagingJdbcTemplate() {
    }

    public PagingJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    public PagingJdbcTemplate(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
    }

    public <T> PaginationResult<T> queryForPage(String sql, Pagination pagination, RowMapper<T> rowMapper) throws DataAccessException {

        return queryForPage(sql, pagination, new PreparedStatementSetter() {
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                return;
            }
        }, rowMapper);
    }


    public <T> PaginationResult<T> queryForPage(String sql, Pagination pagination, PreparedStatementSetter setter, RowMapper<T> mapper) throws DataAccessException {

        PaginationResult<T> result = new PaginationResult<T>();

        //获取记录条数
        String countSql = "select count(1) as count from (" + sql + ") temp";
        List<Integer> countList = super.query(countSql, setter, new RowMapper<Integer>() {
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Integer(resultSet.getInt("count"));
            }
        });
        result.setTotalCount(countList.get(0));
        result.setCurrentPage(pagination.getCurrentPage());
        result.setPageSize(pagination.getPageSize());

        int pageCount = result.getTotalCount() % result.getPageSize();
        result.setTotalPage(pageCount == 0 ? (result.getTotalCount() / result.getPageSize()) : (result.getTotalCount() / result.getPageSize() + 1));

        String sortSql = parseSort(pagination);
        if(sortSql != null){
            sql += sortSql;
        }
        sql += parseLimit(pagination);

        List<T> data = super.query(sql, setter, mapper);
        result.setData(data);

        return result;
    }

    private String parseLimit(Pagination pagination) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" ");
        stringBuffer.append("limit");
        stringBuffer.append(" ");
        stringBuffer.append((pagination.getCurrentPage() - 1) * pagination.getPageSize());
        stringBuffer.append(",");
        stringBuffer.append(pagination.getPageSize());

        return stringBuffer.toString();
    }

    private String parseSort(Pagination pagination) {


        List<SortBy> list = pagination.getSorts();
        if(list.size() == 0){
            return null;
        }

        Collections.sort(list);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" ");
        stringBuffer.append("order by ");
        for(SortBy sortBy : list){
            stringBuffer.append(sortBy.getColName());
            stringBuffer.append(" ");
            stringBuffer.append(sortBy.getSortType());
            stringBuffer.append(",");
        }

        return stringBuffer.toString().substring(0, stringBuffer.length() - 1);

    }


}

package com.jackiew.pagination;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class PagingJdbcTemplateTest {
    @Autowired
    private DataSource dataSource;

    @Test
    public void testQueryForPage() {
        PagingJdbcTemplate template = new PagingJdbcTemplate(dataSource);
        Pagination pagination = new Pagination();
        pagination.setCurrentPage(1);
        pagination.setPageSize(10);
        template.queryForPage("select * from ", pagination, new RowMapper<Object>() {
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Object();
            }
        });

    }
}

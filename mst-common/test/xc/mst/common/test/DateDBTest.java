package xc.mst.common.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

import org.springframework.jdbc.core.ConnectionCallback;
import org.testng.annotations.Test;

public class DateDBTest extends BaseTest {

    @Test
    public void doTest() {
        getJdbcTemplate().execute(new ConnectionCallback<Object>() {
            public Object doInConnection(Connection conn) {
                try {
                    Statement s = null;
                    try {
                        s = conn.createStatement();
                        s.execute("drop table DateDBTest");
                    } catch (Throwable t2) {
                        // do nothing
                    } finally {
                        s.close();
                    }

                    s = conn.createStatement();
                    s.execute("create table DateDBTest ( last_harvest_end_time DATETIME) ENGINE=InnoDB DEFAULT CHARSET=utf8; ");
                    s.close();

                    PreparedStatement ps = conn.prepareStatement("insert into DateDBTest values (?)");

                    ps.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
                    ps.addBatch();

                    ps.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
                    ps.addBatch();

                    ps.setDate(1, new java.sql.Date(0));
                    ps.addBatch();

                    ps.executeBatch();
                    ps.close();
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
                return null;
            }

        });
    }

}

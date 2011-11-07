package org.motechproject.tamafunctional.testdataservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Properties;

@Component
public class ScheduledJobDataService {
    private Properties properties;
    private String QRTZ_JOB_DETAILS_TABLE = "QRTZ_JOB_DETAILS";

    @Autowired
    public ScheduledJobDataService(@Qualifier("quartzProperties") Properties properties) {
        this.properties = properties;

        try {
            String driverClass = property("driver");
            Class.forName(driverClass).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String property(String propertyName) {
        return properties.getProperty(String.format("org.quartz.dataSource.motechDS.%s", propertyName));
    }

    public void clearJobs() {
        Connection connection = null;
        try {
            connection = createConnection();
            clearTable(connection, "QRTZ_CRON_TRIGGERS");
            clearTable(connection, "QRTZ_SIMPLE_TRIGGERS");
            clearTable(connection, "QRTZ_TRIGGERS");
            clearTable(connection, QRTZ_JOB_DETAILS_TABLE);
        } catch (SQLException e) {
            closeHandles(connection, null, null);
            e.printStackTrace();
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(property("URL"), property("user"), property("password"));
    }

    public String currentJobId() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = createConnection();
            preparedStatement = connection.prepareStatement(String.format("select job_name from %s", QRTZ_JOB_DETAILS_TABLE));
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.first()) throw new RuntimeException("Not job not scheduled.");
            return resultSet.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeHandles(connection, preparedStatement, resultSet);
        }
    }

    private void closeHandles(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException ignored) {
        }
    }

    private void clearTable(Connection connection, String tableName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(String.format("delete from %s", tableName));
        preparedStatement.execute();
        preparedStatement.close();
    }
}

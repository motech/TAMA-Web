package org.motechproject.tamafunctional.testdataservice;

import org.apache.commons.lang.SerializationUtils;
import org.joda.time.DateTime;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.util.DateUtil;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

@Component
public class ScheduledJobDataService {
    private SchedulerFactoryBean schedulerFactoryBean;
    private Properties properties;
    private String QRTZ_JOB_DETAILS_TABLE = "QRTZ_JOB_DETAILS";

    @Autowired
    public ScheduledJobDataService(SchedulerFactoryBean schedulerFactoryBean, @Qualifier("quartzProperties") Properties properties) {
        this.schedulerFactoryBean = schedulerFactoryBean;
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

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(property("URL"), property("user"), property("password"));
    }

    public String currentDosageId(String patientId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = createConnection();
            preparedStatement = connection.prepareStatement(String.format("select job_data from %s where job_name like '%s%%'", QRTZ_JOB_DETAILS_TABLE, EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER));
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.first()) throw new RuntimeException("Not job not scheduled.");
            JobDataMap jobDataMap = null;
            while (!resultSet.isAfterLast()) {
                Blob blob = resultSet.getBlob(1);
                InputStream blobStream = blob.getBinaryStream();
                jobDataMap = (JobDataMap) SerializationUtils.deserialize(blobStream);
                if (jobDataMap.get(EventKeys.EXTERNAL_ID_KEY).equals(patientId)) break;
                resultSet.next();
            }
            return jobDataMap.get(EventKeys.DOSAGE_ID_KEY).toString();
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

    public void triggerRedAlertAdherenceJob(String patientId) {
        try {
            DateTime now = DateUtil.now();
            DateTime nowPlus1Minute = now.plusMinutes(1);
            while (nowPlus1Minute.isAfter(DateUtil.now())) {
                String jobName = String.format("%s-%s", TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT, patientId);
                schedulerFactoryBean.getScheduler().triggerJob(jobName, null);
            }
        }
        catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}

/*
 * Copyright 2014 suning.com Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.suning.snfddal.jdbc;

import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.suning.snfddal.command.dml.SetTypes;
import com.suning.snfddal.config.Configuration;
import com.suning.snfddal.config.DataSourceProvider;
import com.suning.snfddal.config.parser.XmlConfigParser;
import com.suning.snfddal.dbobject.User;
import com.suning.snfddal.engine.Database;
import com.suning.snfddal.engine.SessionInterface;
import com.suning.snfddal.message.DbException;
import com.suning.snfddal.message.TraceSystem;
import com.suning.snfddal.util.StringUtils;
import com.suning.snfddal.util.Utils;

/**
 * @author <a href="mailto:jorgie.mail@gmail.com">jorgie li</a>
 */
public class JdbcDataSource implements DataSource {

    private PrintWriter logWriter;
    private int loginTimeout;
    private String userName;
    private String password;
    private String url;

    private String dbType;
    private int defaultQueryTimeout = -1;
    private boolean monitorExecution = true;
    private String validationQuery;
    private int validationQueryTimeout = -1;
    private String exceptionSorterClass;

    private int maxMemoryRows = -1;
    private int maxOperationMemory = -1;

    private String stdoutLevel;
    private String fileLevel;
    private String logFileName;

    private String configLocation;
    private Database database;
    private DataSourceProvider dataSourceProvider;

    private volatile boolean inited = false;

    /**
     * The public constructor.
     */
    public JdbcDataSource() {

    }

    /**
     * Get the login timeout in seconds, 0 meaning no timeout.
     *
     * @return the timeout in seconds
     */
    @Override
    public int getLoginTimeout() {
        return loginTimeout;
    }

    /**
     * Set the login timeout in seconds, 0 meaning no timeout. The default value
     * is 0. This value is ignored by this database.
     *
     * @param timeout the timeout in seconds
     */
    @Override
    public void setLoginTimeout(int timeout) {
        this.loginTimeout = timeout;
    }

    /**
     * Get the current log writer for this object.
     *
     * @return the log writer
     */
    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }

    /**
     * Set the current log writer for this object. This value is ignored by this
     * database.
     *
     * @param out the log writer
     */
    @Override
    public void setLogWriter(PrintWriter out) {
        logWriter = out;
    }

    /**
     * Open a new connection using the current URL, user name and password.
     *
     * @return the connection
     */
    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(Database.SYSTEM_USER_NAME, null);
    }

    /**
     * Open a new connection using the current URL and the specified user name
     * and password.
     *
     * @param user the user name
     * @param password the password
     * @return the connection
     */
    @Override
    public Connection getConnection(String user, String password) throws SQLException {
        User userObject = database.getUser(user);
        SessionInterface session = database.createSession(userObject);
        JdbcConnection conn = new JdbcConnection(session, user, this.configLocation);
        return conn;

    }

    /**
     * Return an object of this class if possible.
     *
     * @param iface the class
     * @return this
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) this;
        }
        throw DbException.getInvalidValueException("iface", iface);
    }

    /**
     * Checks if unwrap can return an object of this class.
     *
     * @param iface the class
     * @return whether or not the interface is assignable from this class
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface != null && iface.isAssignableFrom(getClass());
    }

    /**
     * [Not supported]Java 1.7
     */
    public Logger getParentLogger() {
        return null;
    }

    /**
     * Get the current URL.
     *
     * @return the URL
     */
    public String getURL() {
        return url;
    }

    /**
     * Set the current URL.
     *
     * @param url the new URL
     */
    public void setURL(String url) {
        this.url = url;
    }

    /**
     * Get the current URL. This method does the same as getURL, but this
     * methods signature conforms the JavaBean naming convention.
     *
     * @return the URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the current URL. This method does the same as setURL, but this
     * methods signature conforms the JavaBean naming convention.
     *
     * @param url the new URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Set the current password.
     *
     * @param password the new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the current password.
     *
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Get the current user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the current user name.
     *
     * @param user the new user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "DispatcherDataSource [configLocation=" + configLocation + "]";
    }

    /**
     * @return the configLocation
     */
    public String getConfigLocation() {
        return configLocation;
    }

    /**
     * @param configLocation the configLocation to set
     */
    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * @return the dataSourceProvider
     */
    public DataSourceProvider getDataSourceProvider() {
        return dataSourceProvider;
    }

    /**
     * @param dataSourceProvider the dataSourceProvider to set
     */
    public void setDataSourceProvider(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    /**
     * @return the dbType
     */
    public String getDbType() {
        return dbType;
    }

    /**
     * @param dbType the dbType to set
     */
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    /**
     * @return the validationQuery
     */
    public String getValidationQuery() {
        return validationQuery;
    }

    /**
     * @param validationQuery the validationQuery to set
     */
    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    /**
     * @return the validationQueryTimeout
     */
    public int getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    /**
     * @param validationQueryTimeout the validationQueryTimeout to set
     */
    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    /**
     * @return the exceptionSorterClass
     */
    public String getExceptionSorterClass() {
        return exceptionSorterClass;
    }

    /**
     * @param exceptionSorterClass the exceptionSorterClass to set
     */
    public void setExceptionSorterClass(String exceptionSorterClass) {
        this.exceptionSorterClass = exceptionSorterClass;
    }

    /**
     * @return the stdoutLevel
     */
    public String getStdoutLevel() {
        return stdoutLevel;
    }

    /**
     * @param stdoutLevel the stdoutLevel to set
     */
    public void setStdoutLevel(String stdoutLevel) {
        this.stdoutLevel = stdoutLevel;
    }

    /**
     * @return the fileLevel
     */
    public String getFileLevel() {
        return fileLevel;
    }

    /**
     * @param fileLevel the fileLevel to set
     */
    public void setFileLevel(String fileLevel) {
        this.fileLevel = fileLevel;
    }

    /**
     * @return the logFileName
     */
    public String getLogFileName() {
        return logFileName;
    }

    /**
     * @param logFileName the logFileName to set
     */
    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    /**
     * @return the defaultQueryTimeout
     */
    public int getDefaultQueryTimeout() {
        return defaultQueryTimeout;
    }

    /**
     * @param defaultQueryTimeout the defaultQueryTimeout to set
     */
    public void setDefaultQueryTimeout(int defaultQueryTimeout) {
        this.defaultQueryTimeout = defaultQueryTimeout;
    }

    /**
     * @return the monitorExecution
     */
    public boolean isMonitorExecution() {
        return monitorExecution;
    }

    /**
     * @param monitorExecution the monitorExecution to set
     */
    public void setMonitorExecution(boolean monitorExecution) {
        this.monitorExecution = monitorExecution;
    }

    /**
     * @return the maxMemoryRows
     */
    public int getMaxMemoryRows() {
        return maxMemoryRows;
    }

    /**
     * @param maxMemoryRows the maxMemoryRows to set
     */
    public void setMaxMemoryRows(int maxMemoryRows) {
        this.maxMemoryRows = maxMemoryRows;
    }

    /**
     * @return the maxOperationMemory
     */
    public int getMaxOperationMemory() {
        return maxOperationMemory;
    }

    /**
     * @param maxOperationMemory the maxOperationMemory to set
     */
    public void setMaxOperationMemory(int maxOperationMemory) {
        this.maxOperationMemory = maxOperationMemory;
    }

    public synchronized void init() {
        if (inited) {
            return;
        }
        if (StringUtils.isNullOrEmpty(this.configLocation)) {
            throw new IllegalArgumentException("Property configLocation must not be null");
        }
        InputStream source = Utils.getResourceAsStream(configLocation);
        if (source == null) {
            throw new IllegalArgumentException("Can't load the configLocation resource "
                    + configLocation);
        }
        XmlConfigParser parser = new XmlConfigParser(source);
        Configuration configuration = parser.parse();
        configuration.setProperty(SetTypes.MODE, this.dbType);
        if (this.maxMemoryRows > -1) {
            configuration.setProperty(SetTypes.MAX_MEMORY_ROWS, this.maxMemoryRows);
        }
        if (this.maxOperationMemory > -1) {
            configuration.setProperty(SetTypes.MAX_OPERATION_MEMORY, this.maxOperationMemory);
        }
        configuration.setProperty(SetTypes.MONITOR_EXECUTION, this.monitorExecution);
        configuration.setProperty(SetTypes.VALIDATION_QUERY, this.validationQuery);
        configuration.setProperty(SetTypes.VALIDATION_QUERY_TIMEOUT, this.validationQueryTimeout);
        configuration.setProperty(SetTypes.TRACE_FILE_NAME, this.logFileName);

        if ("TRACE".equalsIgnoreCase(this.stdoutLevel)
                || "DEBUG".equalsIgnoreCase(this.stdoutLevel)) {
            configuration.setProperty(SetTypes.TRACE_LEVEL_SYSTEM_OUT, TraceSystem.DEBUG);
        } else if ("INFO".equalsIgnoreCase(this.stdoutLevel)
                || "WARN".equalsIgnoreCase(this.stdoutLevel)) {
            configuration.setProperty(SetTypes.TRACE_LEVEL_SYSTEM_OUT, TraceSystem.INFO);
        } else if ("ERROR".equalsIgnoreCase(this.stdoutLevel)
                || "FATAL".equalsIgnoreCase(this.stdoutLevel)) {
            configuration.setProperty(SetTypes.TRACE_LEVEL_SYSTEM_OUT, TraceSystem.ERROR);
        } else {
            configuration.setProperty(SetTypes.TRACE_LEVEL_SYSTEM_OUT,
                    TraceSystem.DEFAULT_TRACE_LEVEL_SYSTEM_OUT);
        }
        
        if ("TRACE".equalsIgnoreCase(this.fileLevel) || "DEBUG".equalsIgnoreCase(this.fileLevel)) {
            configuration.setProperty(SetTypes.TRACE_LEVEL_FILE, TraceSystem.DEBUG);
        } else if ("INFO".equalsIgnoreCase(this.fileLevel)
                || "WARN".equalsIgnoreCase(this.fileLevel)) {
            configuration.setProperty(SetTypes.TRACE_LEVEL_FILE, TraceSystem.INFO);
        } else if ("ERROR".equalsIgnoreCase(this.fileLevel)
                || "FATAL".equalsIgnoreCase(this.fileLevel)) {
            configuration.setProperty(SetTypes.TRACE_LEVEL_FILE, TraceSystem.ERROR);
        } else if ("ADAPTER".equalsIgnoreCase(this.fileLevel)) {
            configuration.setProperty(SetTypes.TRACE_LEVEL_FILE, TraceSystem.ADAPTER);
        } else {
            configuration.setProperty(SetTypes.TRACE_LEVEL_FILE,
                    TraceSystem.DEFAULT_TRACE_LEVEL_FILE);
        }
        configuration.setProperty(SetTypes.EXCEPTION_SORTER_CLASS, this.exceptionSorterClass);
        if(this.dataSourceProvider != null) {
            configuration.setDataSourceProvider(this.dataSourceProvider);
        }
        
        this.database = new Database(configuration);
        inited = true;
    }

    public synchronized void close() {
        if (database == null) {
            return;
        }
        database.close();
        database = null;
    }
}

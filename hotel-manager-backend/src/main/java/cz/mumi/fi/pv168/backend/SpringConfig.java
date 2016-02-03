package cz.mumi.fi.pv168.backend;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Filip on 23.4.14.
 */
    @Configuration
    @EnableTransactionManagement
    public class SpringConfig {
    private final static Logger log = LoggerFactory.getLogger(SpringConfig.class);

        @Bean
        public DataSource dataSource() {
            Properties myconf = new Properties();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream("myconf.properties");
            try {
                myconf.load(stream);
            } catch(Exception ex)
            {
                String msg = "Cannot find configuration file";
                log.error(msg, ex);
                throw new ServiceFailureException(msg, ex);
            }

            BasicDataSource bds = new BasicDataSource();
            bds.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
            bds.setUrl(myconf.getProperty("jdbc.dbname"));
            bds.setUsername(myconf.getProperty("jdbc.user"));
            bds.setPassword(myconf.getProperty("jdbc.password"));
            bds.setConnectionProperties("create=true");
            return bds;
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public GuestManager guestManager() {
            return new GuestManagerImpl(new TransactionAwareDataSourceProxy(dataSource()));
        }

        @Bean
        public RoomManager roomManager() {
            return new RoomManagerImpl(new TransactionAwareDataSourceProxy(dataSource()));
        }

        @Bean
        public AccommodationManager accommodationManager() {
            AccommodationManagerImpl accommodationManager = new AccommodationManagerImpl(dataSource());
            accommodationManager.setGuestManager(guestManager());
            accommodationManager.setRoomManager(roomManager());
            return accommodationManager;
        }
    }


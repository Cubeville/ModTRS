package yetanotherx.bukkitplugin.ModTRS.sql;

import com.griefcraft.lwc.DriverStub;
import com.griefcraft.lwc.Updater;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import yetanotherx.bukkitplugin.ModTRS.ModTRS;
import yetanotherx.bukkitplugin.ModTRS.ModTRSSettings;

public class ModTRSMySQL implements IModTRSDatabase {

    private Connection conn;

    @Override
    public void init(ModTRS parent) throws SQLException {

        String databaseUrl = "jdbc:mysql://" + ModTRSSettings.database.get("server") + ":" + ModTRSSettings.database.get("port") + "/" + ModTRSSettings.database.get("database");

	ModTRS.log.debug("Loading MySQL: " + databaseUrl );

	try {
	    URLClassLoader classLoader = new URLClassLoader(new URL[] { new URL("jar:file:" + new File(Updater.DEST_LIBRARY_FOLDER + "lib/mysql.jar") + "!/" ) });
	    Driver driver = (Driver) classLoader.loadClass("com.mysql.jdbc.Driver").newInstance();
	    DriverManager.registerDriver(new DriverStub(driver));
	}
	catch( ClassNotFoundException e) {
	    ModTRS.log.severe("Error: Cannot locate the MySQL JDBC. Please download and place in the plugins/ModTRS/lib folder.");
	    parent.getServer().getPluginManager().disablePlugin(parent);
	}
        catch( Exception e) {
	    throw new SQLException(e.getMessage());
	}

        Properties properties = new Properties();
        properties.put("autoReconnect", "true");
	properties.put("user", ModTRSSettings.database.get("user"));
	properties.put("password", ModTRSSettings.database.get("pass"));

	conn = DriverManager.getConnection(databaseUrl, properties);

	ModTRS.log.debug("Creating tables if necessary" );

        Statement stat = conn.createStatement();
	stat.executeUpdate( this.createUser() );
	stat.executeUpdate( this.createRequest() );

	ModTRS.log.debug("Finished loading SQLite" );

    }

    @Override
    public void close() throws SQLException {
        conn.close();
    }

    @Override
    public PreparedStatement prep(String sql) throws SQLException {
        this.dbExists();

        return conn.prepareStatement(sql);
    }

    @Override
    public Statement stat() throws SQLException {
        this.dbExists();

        return conn.createStatement();
    }

    @Override
    public void dbExists() throws SQLException {
    }


    @Override
    public String createUser() {
        return "CREATE TABLE IF NOT EXISTS `user` (`user_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY , `user_name` TINYTEXT NOT NULL , `user_last_request_id` MEDIUMINT NULL) ENGINE = INNODB;";
    }

    @Override
    public String createRequest() {
        return "CREATE TABLE IF NOT EXISTS `request` (  `request_id` int(11) NOT NULL AUTO_INCREMENT,  `request_user_id` int(11) NOT NULL,  `request_mod_user_id` int(11) NOT NULL DEFAULT '0',  `request_timestamp` bigint(20) NOT NULL,  `request_mod_timestamp` bigint(20) NOT NULL DEFAULT '0',  `request_world` tinytext NOT NULL,  `request_x` mediumint(9) NOT NULL,  `request_y` mediumint(9) NOT NULL,  `request_z` mediumint(9) NOT NULL,  `request_text` text NOT NULL,  `request_status` tinyint(4) DEFAULT '0',  `request_server` text,  `request_mod_comment` text,  PRIMARY KEY (`request_id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    }

    @Override
    public String getUserInfoName() {
        return "SELECT * FROM user WHERE user_name = ? LIMIT 1";
    }

    @Override
    public String getUserInfoId() {
        return "SELECT * FROM user WHERE user_id = ? LIMIT 1";
    }

    @Override
    public String addUserInfo() {
        return "INSERT INTO user (user_name) VALUES (?)";
    }

    @Override
    public String setUserInfo() {
        return "UPDATE user SET user_name=? WHERE user_id=?";
    }

    @Override
    public String getRequestInfo() {
        return "SELECT * FROM request WHERE request_id = ? LIMIT 1";
    }

    @Override
    public String getRequestInfoFromUserId() {
        return "SELECT * FROM request WHERE request_user_id = ? AND request_status < 2";
    }

    @Override
    public String addRequestInfo() {
        return "INSERT INTO request ( request_user_id , request_mod_user_id , request_timestamp , request_mod_timestamp , request_world , request_x , request_y , request_z , request_text ) VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? )";
    }

    @Override
    public String setRequestInfo() {
        return "UPDATE request SET request_user_id = ? , request_mod_user_id = ? , request_timestamp = ? , request_mod_timestamp = ? , request_world = ? , request_x = ? , request_y = ? , request_z = ? , request_text = ? , request_status = ? , request_server = ? , request_mod_comment = ? WHERE request_id = ?";
    }

    @Override
    public String getOpenRequests() {
        return "SELECT * FROM request";
    }

    @Override
    public String updateLocationFieldsToMediumint() {
        return "ALTER TABLE  `request` CHANGE  `request_x`  `request_x` MEDIUMINT NOT NULL ,CHANGE  `request_y`  `request_y` MEDIUMINT NOT NULL ,CHANGE  `request_z`  `request_z` MEDIUMINT NOT NULL";
    }

    @Override
    public String addModCommentField() {
        return "ALTER TABLE  `request` ADD  `request_mod_comment` TEXT NULL";
    }
    
    @Override
    public String addServerField() {
        return "ALTER TABLE  `request` ADD  `request_server` TEXT NULL ";
    }
}
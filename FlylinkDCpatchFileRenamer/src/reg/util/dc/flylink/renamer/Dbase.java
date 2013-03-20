/*
    * Copyright (C) 2013 Developed by reg <entry.reg@gmail.com>
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

package reg.util.dc.flylink.renamer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.*;
import org.sqlite.SQLiteConfig;

public class Dbase
{
    /**
     * Connecting to a database
     */
    private Connection db   = null;
    /**
     * The full path to the database in fs
     */
    private String dbName   = null;
    /**
     * return instance Logger
     */
    private final static Logger logger = Logger.getLogger(FilesRenamer.class.getPackage().getName());
    /**
     * Path of default
     */
    private final static String dbDefault = "Settings/FlylinkDC.sqlite";
    
    /**
     * @param name DB name
     */
    public Dbase(String name)
    {
        dbName = (name == null)? dbDefault : name;
    }
    
    /**
     * Connects to the database, if this has not been done before.
     * @return false - if already connected; true - if new connect to DB.
     * @throws SQLException communication errors
     */
    protected boolean connect() throws SQLException
    {
        if(db != null && !db.isClosed()){
            return false;
        }
        
        try{
            //Class loading and initializing in the DriverManager
            Class.forName("org.sqlite.JDBC");
            
            SQLiteConfig config = new SQLiteConfig();
            config.setReadOnly(true);
            db = DriverManager.getConnection("jdbc:sqlite:" + dbName, config.toProperties());
            return true;
        }
        catch(Exception e){
            throw new SQLException("is not connect to DB", e);
        }
    }
    
    /**
     * Close the active connection
     * @return true - successfully closed; false - the connection was closed previously or an error has occurred
     */
    protected boolean close()
    {
        if(db != null){
            try{
                db.close();
                db = null;
                return true;
            }
            catch(SQLException e){
                logger.log(Level.WARNING, "problem close connection", e);
            }
        }
        return false;
    }
    
    /**
     * Checking connectivity
     * @return 
     */
    public boolean tryToConnect()
    {
        try{
            connect();
            return true;
        }
        catch(Exception e){
            logger.log(Level.WARNING, "cannot connect to '" + dbName + "'", e);
        }
        finally{
//            close();
        }
        return false;
    }
}

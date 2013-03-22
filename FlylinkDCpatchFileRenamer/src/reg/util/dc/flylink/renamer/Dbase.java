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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.*;

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
     * 
     * Таблица fly_path
     * @param from
     * @param to
     * @return 
     */
    protected boolean renamePath(String from, String to)
    {
        int idNew = getExistIdPath(to);
        if(idNew > 0){
            //fix bug #85
            return moveFilesInNewPath(getExistIdPath(from), idNew);
        }
        
        try{
            PreparedStatement pstmt = db.prepareStatement("UPDATE main.fly_path SET name = ? WHERE name = ?");
            pstmt.setString(1, to);
            pstmt.setString(2, from);
            if(pstmt.executeUpdate() > 0){
                logger.log(Level.INFO, "Success: {0} > {1}", new String[]{from, to});
                return true;
            }
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "cannot renamed folder '"+ from +"' to '" + to + "'", e);
        }
        return false;
    }
    
    /**
     * 
     * @param idOld - id старого пути из fly_path
     * @param idNew - id нового пути из fly_path
     * @return 
     */
    private boolean moveFilesInNewPath(int idOld, int idNew)
    {
        try{
            PreparedStatement pstmt = db.prepareStatement("UPDATE fly_file SET dic_path = ? WHERE dic_path = ?");
            pstmt.setInt(1, idNew);
            pstmt.setInt(2, idOld);
            if(pstmt.executeUpdate() > 0){
                return true;
            }
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "cannot move file to a new path '"+ idOld +"' - '"+ idNew + "'", e);
        }        
        return false;        
    }
    
    /**
     * 
     * Таблица fly_file
     * @param from
     * @param to
     * @return 
     */
    protected boolean renameFile(String[] from, String[] to)
    {
        String sql = "UPDATE fly_file SET name = ? "
                   + "WHERE dic_path = (SELECT id FROM fly_path WHERE name = ? LIMIT 1) AND name = ? ";
        try{
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setString(1, to[1]);
            pstmt.setString(2, from[0]);
            pstmt.setString(3, from[1]);
            if(pstmt.executeUpdate() > 0){
                logger.log(Level.INFO, "Success: {0}{1} > {2}{3}", new String[]{from[0], from[1], from[0], to[1]});
                return true;
            }
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "cannot renamed file '"+ from +"' to '" + to + "'", e);
        }        
        return false;
    }
    
    /**
     * id существующего пути.
     * @param path
     * @return -1 если путь не найден.
     */
    protected int getExistIdPath(String path)
    {
        try{
            PreparedStatement pstmt = db.prepareStatement("SELECT id FROM fly_path WHERE name = ?");
            pstmt.setString(1, path);
            ResultSet res = pstmt.executeQuery();
            if(res.next()){
                return res.getInt(0);
            }
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "problem with getExistIdPath by path: '"+ path, e);
        }
        return -1;
    }
    
    /**
     * @param from -> trimmed [0] - path, [1] - file
     * @param to   -> trimmed [0] - path, [1] - file
     * @return 
     */
    public boolean renamePathOrFile(String[] from, String[] to)
    {
        if(!connect()){
            return false;
        }
        if(from[1].length() > 0){
            return renameFile(from, to);
        }
        return renamePath(from[0], to[0]);
    }
    
    public boolean connect()
    {
        try{
            if(db != null && !db.isClosed()){
                return true;
            }
        }
        catch(Exception e){}
        
        try{
            Class.forName("org.sqlite.JDBC");
            db = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            return true;
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "connected failed", e);
        }
        return false;
    }
    
    public boolean close()
    {
        if(db == null){
            return true;
        }
        
        try{
            db.close();
            db = null;
            return true;
        }
        catch(SQLException e){
            logger.log(Level.WARNING, "problem close connection", e);
        }
        return false;
    }
}

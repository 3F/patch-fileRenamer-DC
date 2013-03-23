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
import java.sql.Statement;
import java.util.logging.*;

public class ChangeData
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
    public ChangeData(String name)
    {
        dbName = (name == null)? dbDefault : name;
    }
    
    /**
     * Переименование пути для файлов.
     * Если новый путь уже существует, то все файлы по старому пути будут "перемещены" в новый.
     * Таблица fly_path - записи не удаляются, id записей не меняется.
     * @param from
     * @param to
     * @return 
     */
    protected boolean renamePath(String from, String to)
    {
        int idTo = getExistIdPath(to);
        if(idTo > 0){
            //fix bug #85
            return moveFilesIntoExistPath(getExistIdPath(from), idTo);
        }
        
        try{
            PreparedStatement pstmt = db.prepareStatement("UPDATE fly_path SET name = ? WHERE name = ?");
            pstmt.setString(1, to);
            pstmt.setString(2, from);
            if(pstmt.executeUpdate() > 0){
                if(logger.isLoggable(Level.INFO)){
                    logger.log(Level.INFO, "Success: {0} > {1}", new String[]{from, to});
                }
                return true;
            }
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "cannot renamed folder '"+ from +"' to '" + to + "'", e);
        }
        return false;
    }
    
    /**
     * Переместить все файлы в указанный путь.
     * @param idFrom - id старого пути из fly_path
     * @param idTo - id нового пути из fly_path
     * @return 
     */
    private boolean moveFilesIntoExistPath(int idFrom, int idTo)
    {
        try{
            PreparedStatement pstmt = db.prepareStatement("UPDATE fly_file SET dic_path = ? WHERE dic_path = ?");
            pstmt.setInt(1, idTo);
            pstmt.setInt(2, idFrom);
            if(pstmt.executeUpdate() > 0){
                if(logger.isLoggable(Level.INFO)){
                    logger.log(Level.INFO, "Success[moved-all]: {0} > {1}", new int[]{idFrom, idTo});
                }
                return true;
            }
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "cannot move files to a new path '"+ idFrom +"' - '"+ idTo + "'", e);
        }        
        return false;
    }
    
    /**
     * Переместить файл в новый путь.
     * Если файл перемещается в несуществующий путь, то будет попытка его создать.
     * @param fname
     * @param from
     * @param to
     * @return 
     */
    private boolean moveFile(String fname, String from, String to)
    {
        int idFrom = getExistIdPath(from);
        if(idFrom < 1){ return false; }
        
        int idTo = getExistIdPath(to);
        if(idTo < 1){
            idTo = createPath(to); //try to create
            if(idTo < 1){ return false; }
        }
        
        try{
            PreparedStatement pstmt = db.prepareStatement("UPDATE fly_file SET dic_path = ? WHERE dic_path = ? AND name = ?");
            pstmt.setInt(1, idTo);
            pstmt.setInt(2, idFrom);
            pstmt.setString(3, fname);
            if(pstmt.executeUpdate() > 0){
                if(logger.isLoggable(Level.INFO)){
                    logger.log(Level.INFO, 
                               "Success[moved][{0}]: ({1}){2} > ({3}){4}", 
                               new Object[]{fname, idFrom, from, idTo, to});
                }
                return true;
            }
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "cannot move file("+ fname +") to a new path '"+ from +"' - '"+ to + "'", e);
        }        
        return false;
    }
    
    /**
     * Создание нового пути в таблице fly_path.
     * @param name
     * @return -1 при ошибках
     */
    private int createPath(String name)
    {
        try{
            PreparedStatement pstmt = db.prepareStatement("INSERT INTO fly_path(name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            if(pstmt.executeUpdate() > 0){
                ResultSet res = pstmt.getGeneratedKeys();
                if(res.next()){
                    return res.getInt(1);
                }
            }
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "cannot create path " + name, e);
        }
        return -1;
    }
    
    /**
     * Переименование файла по существующему пути.
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
                if(logger.isLoggable(Level.INFO)){
                    logger.log(Level.INFO, "Success: {0}{1} > {2}{3}", new String[]{from[0], from[1], from[0], to[1]});
                }
                return true;
            }
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "cannot renamed file '"+ from +"' to '" + to + "'", e);
        }        
        return false;
    }
    
    /**
     * Вернуть id существующего пути.
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
                return res.getInt(1);
            }
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "problem with getExistIdPath by path: '"+ path, e);
        }
        return -1;
    }
    
    /**
     * Существует ли файл по указанному пути.
     * @param fname
     * @param path
     * @return 
     */
    public boolean isExistFile(String fname, String path)
    {
        String sql = "SELECT fly_path.id FROM fly_file INNER JOIN fly_path ON fly_file.dic_path = fly_path.id"
                   + " WHERE fly_path.name = ? AND fly_file.name = ?";
        try{
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setString(1, path);
            pstmt.setString(2, fname);
            ResultSet res = pstmt.executeQuery();
            if(res.next()){
                return true;
            }
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "problem with isExistFile by: '" + fname + ", " + path, e);
        }
        return false;
    }
    
    /**
     * @param from -> trimmed [0] - path, [1] - file
     * @param to   -> trimmed [0] - path, [1] - file
     * @return 
     */
    public boolean rename(String[] from, String[] to)
    {
        if(!connect()){
            return false;
        }
        
        /* Files */
        if(from[1].length() > 0){
            // just rename if equals path 
            if(from[0].contentEquals(to[0])){
                return renameFile(from, to);
            }
            
            // if a different path
            if(!from[0].contentEquals(to[0])){
                try{
                    db.setAutoCommit(false);
                    if(isExistFile(to[1], to[0])){
                        logger.log(Level.WARNING, 
                                   "file({0}) already exists in the specified path: {1}", 
                                   new String[]{to[1], to[0]});
                        return false;
                    }
                    
                    // and require file rename
                    if(!from[1].contentEquals(to[1])){
                        if(!renameFile(from, to)){
                            return false;
                        }
                    }                    
                    if(moveFile(to[1], from[0], to[0])){
                        return true;
                    }
                }
                catch(Exception e){
                    logger.log(Level.SEVERE, "rename&move ", e);
                    try{
                        db.rollback();
                    }
                    catch(SQLException eSql){}
                }
                finally{
                    try{
                        db.setAutoCommit(true);
                    }
                    catch(SQLException eSql){}
                }
                return false;
            }
        }
        
        /* Paths */
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

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author reg
 */
public class MainController
{
    private MainForm view               = null;
    private final static Logger logger  = Logger.getLogger(FilesRenamer.class.getPackage().getName());
    
    public MainController(MainForm frm)
    {
        view = frm;
        view.listnerRenameInDb(new actionRenameInDb());
        view.setVisible(true);
    }
    
    /**
     * Events handler for menu - RenameInDb
     */    
    private class actionRenameInDb implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Dbase db = new Dbase(null);
            if(!db.tryToConnect()){
                view.appendListFilesBeforeLine(0, "*[CONNECTED FAILED] place program into flylinkdc folder"
                        + " or try close another program (if used exclusive lock)\n\n");
                return;
            }            
            
            String[] path, renFrom, renTo;
            try{
                int count = view.getCountListFilesLines();
                for(int i = 0; i < count; i++){
                    String text = view.getLineFromListFiles(i);
                    
                    if(!FilesGetting.verifyLine(text)){
                        view.appendListFilesBeforeLine(i, "[ERROR-FORMAT]: ");
                        continue;
                    }
                    
                    path = FilesGetting.get2Path(text);
                    try{
                        renFrom = FilesGetting.splitPath(path[0]);
                        renTo   = FilesGetting.splitPath(path[1]);
                    }
                    catch(Exception ex){
                        view.appendListFilesBeforeLine(i, "[ERROR-PARSE]: ");
                        continue;
                    }
                    
                    //Связи переименовния должны быть только: каталог-каталог, файл-файл
                    if((renFrom[1].length() < 1 && renTo[1].length() > 0)
                            ||
                       (renFrom[1].length() > 0 && renTo[1].length() < 1)
                      ){
                        view.appendListFilesBeforeLine(i, "[ERROR-DIFF]: ");
                        continue;
                    }
                    
                    if(!db.renamePathOrFile(renFrom, renTo)){
                        view.appendListFilesBeforeLine(i, "[ERROR-DB]: ");
                        continue;
                    }
                    view.appendListFilesBeforeLine(i, "[OK]: ");
                }
            }
            catch(IndexOutOfBoundsException ex){
                logger.log(Level.SEVERE, ex.getMessage());
                System.out.println(ex.getMessage());
            }
        }
    }      
}

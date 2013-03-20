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

/**
 *
 * @author reg
 */
public class MainController
{
    private MainForm view = null;
    
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
            try{
                //System.out.println(view.getLineFromListFiles(4));
                view.appendListFilesBeforeLine(3, "---> ");
            }
            catch(IndexOutOfBoundsException ex){
                System.out.println(ex.getMessage());
            }
        }
    }      
}

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

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import reg.util.dc.flylink.renamer.exceptions.IllegalOperationException;

/**
 *
 * @author reg
 */
public class FilesRenamer
{
    private final static Logger logger = Logger.getLogger(FilesRenamer.class.getPackage().getName());
    
    public static void main(String[] args)
    {
        try{
            LogManager logMan = LogManager.getLogManager();
            logMan.readConfiguration(FilesRenamer.class.getResourceAsStream("logging.properties"));
        }
        catch(IOException e){
            System.err.println("cannot read configuration(logging.properties) from resource");
            e.printStackTrace();
            System.exit(IllegalOperationException.EXIT_CODE_CONFIG_READ_ERROR);
        }
        new MainController(new MainForm());
    }
}


    


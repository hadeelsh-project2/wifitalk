/*
 */
package sa.edu.ksu.pnes.snort;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;


/**
 */
public class CommandRunner {

    
    public static Process startCommand(String command, String options, String logFile) throws IOException {
    	
        if (command == null || options == null) {
            System.out.println("Error: no command is given to run");
        }
        var opts = options.split(" ");
        var cmd = new String[opts.length + 1];
        cmd[0] = command;
        for(int i = 0; i < opts.length; i++) {
        	cmd[i + 1] = opts[i];
        }
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        builder.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(logFile)));
        
        Process p = builder.start();
        return p;

    }

    public static void shutDown(Process p) {
        if (p != null && p.isAlive()) {
            p.destroy();
        }
    }
    
 
    public static boolean  executeIptables(String platform, String... cmd) {
    	boolean result = false;
		ProcessBuilder builder = new ProcessBuilder(cmd);     
		try {
			Process p = builder.start();
			BufferedReader br = new BufferedReader(p.inputReader());
			String line = br.readLine();
			if (line == null && platform.equalsIgnoreCase(Platforms.LINUX)) {
					result = true;
			}
            else if(platform.equalsIgnoreCase(Platforms.WINDOWS))
            {
                while(line != null)
                {
                    if(line.trim().toLowerCase().contains("ok"))
                    {
                        result = true;
                        break;
                    }
                    line = br.readLine();
                }

            }
			br.close();
			if(p!=null && p.isAlive())
			{
				p.destroy();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

 
}

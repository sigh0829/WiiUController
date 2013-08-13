/*

 Copyright (c) 2013, Ned Hyett
 All rights reserved.

 By using this program/package/library you agree to be completely and unconditionally
 bound by the agreement displayed below. Any deviation from this agreement will not
 be tolerated.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice, this
 list of conditions and the following disclaimer in the documentation and/or other
 materials provided with the distribution.
 3. The redistribution is not sold, unless permission is granted from the copyright holder.
 4. The redistribution must contain reference to the original author, and this page.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package nintendofan9797.WiiUController;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Ned
 */
public class WiiUController {

    private static boolean move_mouse = false;
    private static int mx = 0;
    private static int my = 0;
    
    private static boolean right = false;
    private static boolean left = false;
    private static boolean l1 = false;
    private static boolean r1 = false;
    
    private static final int sensitivity = 25;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
	Robot rob = new Robot(); //Hey rob!
	ServerSocket ss = new ServerSocket(8080);
	while(true){
	    Socket s = ss.accept();
	    Scanner sc = new Scanner(s.getInputStream());
	    while(sc.hasNextLine()){
		String next = sc.nextLine();
		if(next.equals("")){
		    next = sc.nextLine();
		    //System.out.println(next);
		    HashMap<String, Integer> keys = parseRead(next);
		    if(keys.get("zl") == 1){
			left = true;
		    } else {
			left = false;
		    }
		    if(left && !l1){
			rob.mousePress(InputEvent.BUTTON1_MASK);
			l1 = true;
		    } else {
			if(!left && l1) {
			    rob.mouseRelease(InputEvent.BUTTON1_MASK);
			    l1 = false;
			}
		    }
		    if(keys.get("zr") == 1){
			right = true;
		    } else {
			right = false;
		    }
		    if(right && !r1){
			rob.mousePress(InputEvent.BUTTON3_MASK);
			r1 = true;
		    } else {
			if(!right && r1) {
			    rob.mouseRelease(InputEvent.BUTTON3_MASK);
			    r1 = false;
			}
		    }
		    
		    if(keys.get("r_stick_left") == 1){
			mx-=sensitivity;
		    }
		    if(keys.get("r_stick_right") == 1){
			mx+=sensitivity;
		    }
		    if(keys.get("r_stick_up") == 1){
			my-=sensitivity;
		    }
		    if(keys.get("r_stick_down") == 1){
			my+=sensitivity;
		    }
		    
		    if(keys.get("plus") == 1){
			move_mouse = true;
		    }
		    if(keys.get("minus") == 1){
			move_mouse = false;
		    }
		    if(move_mouse){
			//System.out.println(mx + " : " + my);
			rob.mouseMove(mx, my);
			Point p = MouseInfo.getPointerInfo().getLocation();
			//System.out.println(p.x + " @ " + p.y);
			mx = 681;
			my = 386;
		    }
		    if(keys.get("l_stick_up") == 1){
			//System.out.println("FORWARD");
			rob.keyPress(KeyEvent.VK_UP);
		    } else {
			rob.keyRelease(KeyEvent.VK_UP);
		    }
		    if(keys.get("l_stick_down") == 1){
			rob.keyPress(KeyEvent.VK_DOWN);
		    } else {
			rob.keyRelease(KeyEvent.VK_DOWN);
		    }
		    if(keys.get("l_stick_left") == 1){
			rob.keyPress(KeyEvent.VK_LEFT);
		    } else {
			rob.keyRelease(KeyEvent.VK_LEFT);
		    }
		    if(keys.get("l_stick_right") == 1){
			rob.keyPress(KeyEvent.VK_RIGHT);
		    } else {
			rob.keyRelease(KeyEvent.VK_RIGHT);
		    }
		    
		    if(keys.get("a") == 1){
			rob.keyPress(KeyEvent.VK_SPACE);
			rob.keyRelease(KeyEvent.VK_SPACE);
		    }
		    
		    if(keys.get("x") == 1){
			rob.keyPress(KeyEvent.VK_SHIFT);
			rob.keyRelease(KeyEvent.VK_SHIFT);
		    }
		}
	    }
	    PrintWriter out = new PrintWriter(s.getOutputStream());
	    out.println("HTTP/1.1 200 OK");
	    out.println("Connection: close");
	    out.println();
	    out.flush();
	    out.close();
	    sc.close();
	}
    }
    
    
    private static HashMap<String, Integer> parseRead(String line){
	HashMap<String, Integer> ret = new HashMap<>();
	line = line.replace("gamepadstate={", "");
	line = line.replace("}", "");
	String[] params = line.split(",");
	for(String param : params){
	    String[] split = param.split(":");
	    String key = split[0].replace("\"", "");
	    Integer val = Integer.parseInt(split[1]);
	    ret.put(key, val);
	}
	return ret;
    }
    
}

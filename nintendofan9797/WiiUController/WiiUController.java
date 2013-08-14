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
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Ned
 */
public class WiiUController {

    /**
     * Should rob take control of mouse?
     */
    private static boolean move_mouse = false;
    
    
    /**
     * Right mouse binding held
     */
    private static boolean right = false;
    
    /**
     * Left mouse binding held
     */
    private static boolean left = false;
    
    /**
     * Left mouse binding released
     */
    private static boolean l1 = false;
    
    /**
     * Right mouse binding released
     */
    private static boolean r1 = false;
    
    /**
     * How far to move when stuff.
     */
    private static final int sensitivity = 50;
    
    private static boolean p_pressed = false;    

    public static void main(String[] args) {
	try {
	    main1(args);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * @param args the command line arguments
     */
    public static void main1(String[] args) throws Exception {
	Robot rob = new Robot(); //Hey rob!
	ServerSocket ss = new ServerSocket(8080);
	System.out.println("STARTED ON PORT 8080!");
	while (true) {
	    try {
		Socket s = ss.accept();

		//System.out.println("ACCEPT");
		Scanner sc = new Scanner(s.getInputStream());
		String command = sc.nextLine();
		//System.out.println(command);
		if (command.split(" ")[1].equals("/wiiu")) {
		    PrintWriter out = new PrintWriter(s.getOutputStream());
		    out.println("HTTP/1.1 200 OK");
		    out.println("Content-Type: text/html");
		    out.println();
		    out.println(getPage());
		    out.flush();
		    out.close();
		    sc.close();
		    //System.out.println("SENT");
		    continue;
		}
		if (command.split(" ")[1].equals("/39.gif")) {
		    InputStream gif39 = ClassLoader.getSystemClassLoader().getResourceAsStream("nintendofan9797/WiiUController/39.gif");
		    byte[] buf = new byte[gif39.available()];
		    gif39.read(buf);
		    PrintWriter pw = new PrintWriter(s.getOutputStream());
		    pw.println("HTTP/1.1 200 OK");
		    pw.println("Content-Type: image/gif");
		    pw.println();
		    pw.flush();
		    s.getOutputStream().write(buf);
		    pw.close();
		}
		if (command.split(" ")[1].equals("/favicon.ico")) {
		    PrintWriter pw = new PrintWriter(s.getOutputStream());
		    pw.println("HTTP/1.1 404 Not Found");
		    pw.println();
		    pw.flush();
		    pw.close();
		    continue;
		}
		if (!command.split(" ")[1].equals("/control")) {
		    continue;
		}
		while (sc.hasNextLine()) {
		    String next = sc.nextLine();
		    if (next.equals("")) {
			next = sc.nextLine();
			//System.out.println(next);
			HashMap<String, Float> keys = parseRead(next);
			if (keys.get("zl") == 1) {
			    left = true;
			} else {
			    left = false;
			}
			if (left && !l1) {
			    rob.mousePress(InputEvent.BUTTON1_MASK);
			    l1 = true;
			} else {
			    if (!left && l1) {
				rob.mouseRelease(InputEvent.BUTTON1_MASK);
				l1 = false;
			    }
			}
			if (keys.get("zr") == 1) {
			    right = true;
			} else {
			    right = false;
			}
			if (right && !r1) {
			    rob.mousePress(InputEvent.BUTTON3_MASK);
			    r1 = true;
			} else {
			    if (!right && r1) {
				rob.mouseRelease(InputEvent.BUTTON3_MASK);
				r1 = false;
			    }
			}

		

			if (keys.get("plus") == 1) {
			    if(!p_pressed){
			    move_mouse = !move_mouse;
			    p_pressed = true;
			    }
			} else { 
			    if(p_pressed){
				p_pressed = false;
			    }
			}
			
			if(keys.get("minus") == 1){
			    rob.keyPress(KeyEvent.VK_Z);
			} else {
			    rob.keyRelease(KeyEvent.VK_Z);
			}
			
			if (move_mouse) {
			    //System.out.println(mx + " : " + my);
			    Point p = MouseInfo.getPointerInfo().getLocation();
			    float x = sensitivity * keys.get("r_x_deflect");
			    float y = sensitivity * keys.get("r_y_deflect");
			    //System.out.println("MOVING TO: " + x + " ~ " + y);
			    
			    rob.mouseMove((int)(p.x + x), (int)(p.y - y));
//			    System.out.println(p.x + " @ " + p.y);
			}
			if (keys.get("l_stick_up") == 1) {
			    //System.out.println("FORWARD");
			    rob.keyPress(KeyEvent.VK_UP);
			} else {
			    rob.keyRelease(KeyEvent.VK_UP);
			}
			if (keys.get("l_stick_down") == 1) {
			    rob.keyPress(KeyEvent.VK_DOWN);
			} else {
			    rob.keyRelease(KeyEvent.VK_DOWN);
			}
			if (keys.get("l_stick_left") == 1) {
			    rob.keyPress(KeyEvent.VK_LEFT);
			} else {
			    rob.keyRelease(KeyEvent.VK_LEFT);
			}
			if (keys.get("l_stick_right") == 1) {
			    rob.keyPress(KeyEvent.VK_RIGHT);
			} else {
			    rob.keyRelease(KeyEvent.VK_RIGHT);
			}
			
			if(keys.get("r") == 1){
			    rob.keyPress(KeyEvent.VK_Q);
			} else {
			    rob.keyRelease(KeyEvent.VK_Q);
			}
			
			if(keys.get("l_stick_press") == 1){
			    rob.keyPress(KeyEvent.VK_G);
			} else {
			    rob.keyRelease(KeyEvent.VK_G);
			}
			
			if(keys.get("r_stick_press") == 1){
			    rob.keyPress(KeyEvent.VK_ESCAPE);
			} else {
			    rob.keyRelease(KeyEvent.VK_ESCAPE);
			}

			if (keys.get("a") == 1) {
			    rob.keyPress(KeyEvent.VK_SPACE);
			} else {
			    rob.keyRelease(KeyEvent.VK_SPACE);
			}

			if (keys.get("x") == 1) {
			    rob.keyPress(KeyEvent.VK_SHIFT); 
			} else {
			    rob.keyRelease(KeyEvent.VK_SHIFT);
			}
			
			if(keys.get("up") == 1){
			    rob.mouseWheel(1);
			}
			if(keys.get("down") == 1){
			    rob.mouseWheel(-1);
			}
			
			if(keys.get("left") == 1){
			    rob.keyPress(KeyEvent.VK_R);
			} else {
			    rob.keyRelease(KeyEvent.VK_R);
			}
			if(keys.get("right") == 1){
			    rob.keyPress(KeyEvent.VK_X);
			} else {
			    rob.keyRelease(KeyEvent.VK_X);
			}
			
			if(keys.get("tt") == 1){
			    rob.mouseMove((int)(keys.get("tt_x") + 0), (int)(keys.get("tt_y") + 0));
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
	    } catch (Exception e) {
	    }
	}
    }

    private static HashMap<String, Float> parseRead(String line) {
	HashMap<String, Float> ret = new HashMap<>();
	line = line.replace("gamepadstate={", "");
	line = line.replace("}", "");
	String[] params = line.split(",");
	for (String param : params) {
	    String[] split = param.split(":");
	    String key = split[0].replace("\"", "");
	    float val = Float.parseFloat(split[1]);
	    ret.put(key, val);
	}
	return ret;
    }

    private static String getPage() throws Exception {
	String page = "<!doctype html>\n"
		+ "<html>\n"
		+ "    <head>\n"
		+ "        <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />\n"
		+ "        <meta name=\"viewport\" content=\"width=1280 height=598 user-scalable=no\" />\n"
		+ "		<script src=\"http://code.jquery.com/jquery-2.0.3.min.js\"></script> <!-- Replace this path with a valid version of JQuery -->\n"
		+ "		<style>\n"
		+ "			.fp {\n"
		+ "				width: 200px;\n"
		+ "				height: 40px;\n"
		+ "				padding: 3px 8px;\n"
		+ "				color: #ffffff;\n"
		+ "				background-color: #000000;\n"
		+ "				border-radius: 4px;\n"
		+ "				position: absolute;\n"
		+ "			}\n"
		+ "		</style>\n"
		+ "        <title>Wii U Controller (Nintendofan9797)</title>\n"
		+ "        <script type=\"text/javascript\">\n"
		+ "            var xhReq = new XMLHttpRequest();\n"
		+ "\n"
		+ "            function init()\n"
		+ "            {\n"
		+ "                if(window.wiiu)\n"
		+ "                {\n"
		+ "					window.history.forward();\n"
		+ "                    setInterval('update()', 40);\n"
		+ "                } else {\n"
		+ "					alert(\"This page is only compatible with the Wii U internet browser!\");\n"
		+ "					window.history.back();\n"
		+ "				}\n"
		+ "            }\n"
		+ "\n"
		+ "            function update()\n"
		+ "            {\n"
		+ "                var gamepadState = window.wiiu.gamepad.update();\n"
		+ "                if(gamepadState.isEnabled && gamepadState.isDataValid)\n"
		+ "                {\n"
		+ "					document.getElementById(\"HELD\").innerHTML = \"HELD MASK: \" + gamepadState.hold;\n"
		+ "					\n"
		+ "					if(gamepadState.tpTouch){\n"
		+ "						document.getElementById(\"TOUCH\").innerHTML = \"TOUCH: YEP! ~ X: \" + gamepadState.contentX + \" Y: \" + gamepadState.contentY;\n"
		+ "						$(\"#trains\").css({left: (gamepadState.contentX - 64) + 'px', top: (gamepadState.contentY - 64) + 'px'});\n"
		+ "					} else {\n"
		+ "						document.getElementById(\"TOUCH\").innerHTML = \"TOUCH: NOPE\";\n"
		+ "					}\n"
		+ "					\n"
		+ "					var i;\n"
		+ "					var mask = 0x80000000;\n"
		+ "					var props = {r: 0, l_stick_left: 0, l_stick_right: 0, l_stick_up: 0, l_stick_down: 0, r_stick_left: 0, r_stick_right: 0, r_stick_up: 0, r_stick_down: 0, l_stick_press: 0, r_stick_press: 0, a: 0, b: 0, x: 0, y: 0, left: 0, right: 0, up: 0, down: 0, zl: 0, zr: 0, plus: 0, minus: 0, tt_x: 0, tt_y: 0, tt: 0, l_x_deflect: 0, l_y_deflect: 0, r_x_deflect: 0, r_y_deflect: 0};\n"
		+ "					for(i = 0; i < 59; i += 2, mask = (mask >>> 1)){\n"
		+ "						var isHeld = (gamepadState.hold & 0x7f86fffc & mask) ? 1: 0;\n"
		+ "						if(i == 2) props.l_stick_left = (isHeld? 1 : 0);\n"
		+ "						if(i == 4) props.l_stick_right = (isHeld? 1 : 0);\n"
		+ "						if(i == 6) props.l_stick_up = (isHeld? 1 : 0);\n"
		+ "						if(i == 8) props.l_stick_down = (isHeld? 1 : 0);\n"
		+ "						if(i == 10) props.r_stick_left = (isHeld? 1 : 0);\n"
		+ "						if(i == 12) props.r_stick_right = (isHeld? 1 : 0);\n"
		+ "						if(i == 14) props.r_stick_up = (isHeld? 1 : 0);\n"
		+ "						if(i == 16) props.r_stick_down = (isHeld? 1 : 0);\n"
		+ "						if(i == 26) props.l_stick_press = (isHeld? 1 : 0);\n"
		+ "						if(i == 28) props.r_stick_press = (isHeld? 1 : 0);\n"
		+ "						if(i == 32) props.a = (isHeld? 1 : 0);\n"
		+ "						if(i == 36) props.x = (isHeld? 1 : 0);\n"
		+ "						if(i == 40) props.left = (isHeld? 1 : 0);\n"
		+ "						if(i == 42) props.right = (isHeld? 1 : 0);\n"
		+ "						if(i == 44) props.up = (isHeld? 1 : 0);\n"
		+ "						if(i == 46) props.down = (isHeld? 1 : 0);\n"
		+ "						if(i == 48) props.zl = (isHeld? 1 : 0);\n"
		+ "						if(i == 50) props.zr = (isHeld? 1 : 0);"
		+ "if(i == 54) props.r = (isHeld? 1 : 0);\n"
		+ "						if(i == 56) props.plus = (isHeld? 1 : 0);\n"
		+ "						if(i == 58) props.minus = (isHeld? 1 : 0);\n"
		+ "					}\n"
		+ "					if(gamepadState.tpTouch){\n"
		+ "						props.tt = 1;\n"
		+ "						props.tt_x = gamepadState.contentX;\n"
		+ "						props.tt_y = gamepadState.contentY;\n"
		+ "					}"
		+ "props.l_x_deflect = gamepadState.lStickX;"
		+ "props.l_y_deflect = gamepadState.lStickY;"
		+ "props.r_x_deflect = gamepadState.rStickX;"
		+ "props.r_y_deflect = gamepadState.rStickY;\n"
		+ "                    xhReq.open(\"POST\", \"http://" + InetAddress.getLocalHost().getHostAddress() + ":8080/control\", true);\n"
		+ "                    xhReq.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n"
		+ "                    xhReq.send(\"gamepadstate=\" + JSON.stringify(props));\n"
		+ "                }\n"
		+ "				document.getElementById(\"ACCEL\").innerHTML = \"Acceleromter:  (X: \" + gamepadState.accX.toFixed(3) + \" ~ Y: \" + gamepadState.accY.toFixed(3) + \" ~ Z: \" + gamepadState.accZ.toFixed(3) + \")\";\n"
		+ "				document.getElementById(\"GYRO\").innerHTML = \"Gyro: (X: \" + gamepadState.gyroX.toFixed(3) + \" ~ Y: \" + gamepadState.gyroY.toFixed(3) + \" ~ Z: \" + gamepadState.gyroZ.toFixed(3) + \")\";\n"
		+ "				document.getElementById(\"ANG\").innerHTML = \"Angle: (X: \" + gamepadState.angleX.toFixed(3) + \" ~ Y: \" + gamepadState.angleY.toFixed(3) + \" ~ Z: \" + gamepadState.angleZ.toFixed(3) + \")\";\n"
		+ "				document.getElementById(\"DEFLECTL\").innerHTML = \"Deflector L: (X: \" + gamepadState.lStickX + \" ~ Y: \" + gamepadState.lStickY + \")\";"
		+ "				document.getElementById(\"DEFLECTR\").innerHTML = \"Deflector R: (X: \" + gamepadState.rStickX + \" ~ Y: \" + gamepadState.rStickY + \")\";"
		+ "            }\n"
		+ "			\n"
		+ "        </script>\n"
		+ "    </head>\n"
		+ "    <body onload=\"init();\">\n"
		+ "		<h2 id=\"HELD\">HELD MASK: NONE</h2>\n"
		+ "		<h2 id=\"TOUCH\">TOUCH: NOPE</h2>\n"
		+ "		<h2 id=\"ACCEL\">Acceleromter: (X) ~ (Y) ~ (Z)</h2>\n"
		+ "		<h2 id=\"GYRO\">Gyro: (X) ~ (Y) ~ (Z)</h2>\n"
		+ "		<h2 id=\"ANG\">Angle: (X) ~ (Y) ~ (Z)</h2>\n"
		+ "		<h2 id=\"ORIENTX\">OrientationX: (X) ~ (Y) ~ (Z)</h2>\n"
		+ "		<h2 id=\"ORIENTY\">OrientationY: (X) ~ (Y) ~ (Z)</h2>\n"
		+ "		<h2 id=\"ORIENTZ\">OrientationZ: (X) ~ (Y) ~ (Z)</h2>\n"
		+ "<h2 id=\"DEFLECTL\">Deflector L: (X) ~ (Y)</h2>\n"
		+ "<h2 id=\"DEFLECTR\">Deflector R: (X) ~ (Y)</h2>\n"
		+ "<h2>To start using mouse control, press + then move the right stick. Press + again to stop mouse control.</h2>"
		+ "		\n"
		+ "    </body>\n"
		+ "	<img id=\"trains\" src=\"/39.gif\" width=\"128px\" height=\"128px\" style=\"position: absolute;\"></img>\n"
		+ "	\n"
		+ "	\n"
		+ "	\n"
		+ "</html>";

	return page;

    }

}

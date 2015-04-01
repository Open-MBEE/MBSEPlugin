/*
 * 
 *    (c) European Southern Observatory, 2011
 *    Copyright by ESO 
 *    All rights reserved
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 *    
 *    $Id: regexpTester.java 238 2011-11-04 16:50:04Z jesdabod $
 *
*/

package org.eso.sdd.mbse.doc.algo;


import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class regexpTester {

	private static String le = System.getProperty("line.separator");
	private static String theString = "<head>" + le + "egal" + le + "</head>" + le;
	/*
	  <body>
	    <table cellpadding="0" width="100%" cellspacing="0">
	      <tr>
	        <td>
	          <b>&#160;c1</b>
	        </td>
	        <td>
	          <b>&#160;c2</b>
	        </td>
	      </tr>
	      <tr>
	        <td>
	          &#160;a
	        </td>
	        <td>
	          &#160;b
	        </td>
	      </tr>
	    </table>
	  </body>
	</html>";
*/

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pattern = "<head>.*</head>";
		theString = "<head>" + le + "<something>" + le + "content" + le + "</something>" + le + "</head>" + le + "<andagain>" + le;
		Pattern pa = Pattern.compile(pattern,Pattern.DOTALL);
		Matcher m = pa.matcher(theString);
		if (m.find()) { 
			System.out.println("Matched (" + theString + " with " + pattern);
			System.out.println("");
			System.out.println("Match: "+  m.group() + " starts at " +m.start() + " ends at " +  m.end());
			System.out.println(m.replaceFirst("DEADBEEF"));
		} else {
			System.out.println("Did NOT match ("+ theString + ")");
		}
		

	}

}

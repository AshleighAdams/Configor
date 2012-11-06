/*
	A little config thingy for C++ and C#
	Created by C0BRA
	Copyright XiaTek.org 2012
	Released under the MIT licence
*/

package Configor;

// ********* THIS CODE IS AUTO PORTED FROM C# TO JAVA USING CODEPORTING.COM TECHNOLOGY *********

import com.codeporting.csharp2java.java.Enum;
import com.codeporting.csharp2java.System.Text.Encoding;
import com.codeporting.csharp2java.System.Convert;
import com.codeporting.csharp2java.System.msString;
import com.codeporting.csharp2java.System.IO.MemoryStream;
import com.codeporting.csharp2java.System.IO.StreamReader;
import com.codeporting.csharp2java.System.IO.BinaryReader;
import com.codeporting.csharp2java.System.Text.msStringBuilder;
import com.codeporting.csharp2java.System.IO.StreamWriter;
import com.codeporting.csharp2java.System.IO.File;
import com.codeporting.csharp2java.System.IO.TextWriter;
import com.codeporting.csharp2java.System.IO.SeekOrigin;

public /*enum*/ final class LexType extends Enum
{
	private LexType(){}	
	public static final int NONE = 0;
	public static final int TOKEN = 1;
	public static final int STRING = 2;
	public static final int NEW_LINE = 3;

	static {
		Enum.register(new Enum.SimpleEnum(LexType.class, Integer.class) {{
		addConstant("NONE", NONE);
		addConstant("TOKEN", TOKEN);
		addConstant("STRING", STRING);
		addConstant("NEW_LINE", NEW_LINE);
		}});
	}


}

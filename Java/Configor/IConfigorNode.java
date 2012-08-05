/*
	A little config thingy for C++ and C#
	Created by C0BRA
	Copyright XiaTek.org 2012
	Released under the MIT licence
*/

package Configor;

// ********* THIS CODE IS AUTO PORTED FROM C# TO JAVA USING CODEPORTING.COM TECHNOLOGY *********

import java.util.LinkedList;

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


public interface IConfigorNode
{
	public String getName();
	public byte[] getData();
	public void setData(byte[] pData);
	public void setString(String Data);
	public String getString();
	public  <T>T getValue(T Default);
	public  <T>void setValue(T Value);
	public IConfigorNode getChild(String Name);
	public IConfigorNode getParent();
	public void addChild(IConfigorNode Node);
	public void removeChild(IConfigorNode Node);
	public LinkedList< IConfigorNode> getChildren();
	public IConfigorNode get(String i);
}

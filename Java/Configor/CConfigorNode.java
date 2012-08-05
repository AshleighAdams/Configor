/*
	A little config thingy for C++ and C#
	Created by C0BRA
	Copyright XiaTek.org 2012
	Released under the MIT licence
*/

package Configor;

// ********* THIS CODE IS AUTO PORTED FROM C# TO JAVA USING CODEPORTING.COM TECHNOLOGY *********

import java.util.LinkedList;

import com.codeporting.csharp2java.ms;
import com.codeporting.csharp2java.java.Enum;
import com.codeporting.csharp2java.System.Text.Encoding;
import com.codeporting.csharp2java.System.Convert;
import com.codeporting.csharp2java.System.TypeCode;
import com.codeporting.csharp2java.System.msString;
import com.codeporting.csharp2java.System.Globalization.CultureInfo;
import com.codeporting.csharp2java.System.IO.MemoryStream;
import com.codeporting.csharp2java.System.IO.StreamReader;
import com.codeporting.csharp2java.System.IO.BinaryReader;
import com.codeporting.csharp2java.System.Text.msStringBuilder;
import com.codeporting.csharp2java.System.IO.StreamWriter;
import com.codeporting.csharp2java.System.IO.File;
import com.codeporting.csharp2java.System.IO.TextWriter;
import com.codeporting.csharp2java.System.IO.SeekOrigin;

import java.lang.reflect.ParameterizedType;



public class CConfigorNode implements IConfigorNode
{
	class TTT<B> 
	{
	        public Class<B> g() throws Exception
	        {
	                ParameterizedType superclass = (ParameterizedType)getClass().getGenericSuperclass();

	                return (Class<B>) superclass.getActualTypeArguments()[0];
	        }
	}
	
	static byte[] _GetBytes(String str)
	{
		return Encoding.UTF8.getBytes(str);
		//byte[] bytes = new byte[str.Length * sizeof(char)];
		//System.Buffer.BlockCopy(str.ToCharArray(), 0, bytes, 0, bytes.Length);
		//return bytes;
	}

	static String _GetString(byte[] bytes)
	{
		if (bytes == null)
			return "";
		return Encoding.UTF8.getString(bytes);
		//if (bytes == null)
		//	return "";
		//char[] chars = new char[bytes.Length / sizeof(char)];
		//System.Buffer.BlockCopy(bytes, 0, chars, 0, bytes.Length);
		//return new string(chars);
	}

	public CConfigorNode(IConfigorNode Parent, String Name)
	{
		_Data = null;
		_Children = new LinkedList< IConfigorNode>();
		_Name = Name;
		_Parent = Parent;
		if (_Parent != null)
			_Parent.addChild(this);
	}


	public IConfigorNode get(String i)
	{
		IConfigorNode n = this.getChild(i);
		if (n == null)
			return new CConfigorNode(this, i);
		return n;
	}

	public String getName()
	{
		return _Name;
	}

	public byte[] getData()
	{
		return _Data;
	}

	public void setData(byte[] Data)
	{
		_Data = Data;
	}

	public void setString(String Data)
	{
		_Data = _GetBytes(Data);
	}

	public String getString()
	{
		return _GetString(_Data);
	}
	
	public static Object Convert_Func(String in, Object type) throws Exception
	{
		if(type instanceof Integer)
			return Integer.parseInt(in);
		if(type instanceof Float)
			return Float.parseFloat(in);
		if(type instanceof Double)
			return Double.parseDouble(in);
		if(type instanceof Boolean)
			return Boolean.parseBoolean(in);
		if(type instanceof Byte)
			return Byte.parseByte(in);
		if(type instanceof Long)
			return Long.parseLong(in);
		if(type instanceof Short)
			return Short.parseShort(in);
		
		if(type instanceof String)
		{
			if(in.length() == 0)
				throw new Exception("length 0");
			return in;
		}
		
		throw new Exception("Unknown type");
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(T Default)
	{
		if (_Data == null)
		{
			this.setValue(Default);
			return Default;
		}

		//return (T)Convert.changeType(getString(), ms.typeOf(T.class));
		try {
			return (T) Convert_Func(getString(), (Object)Default);
			
		} catch (Exception e) {
			this.setValue(Default);
			return Default;
		}
	}

	public void setValue(Object Value)
	{
		this.setString(Value.toString());
	}

	public IConfigorNode getChild(String i)
	{
		for (IConfigorNode node : (Iterable<IConfigorNode>) _Children)
			if (msString.equals(node.getName(), i))
				return node;
		return null;
	}

	public IConfigorNode getParent()
	{
		return _Parent;
	}

	public void addChild(IConfigorNode node)
	{
		_Children.addLast(node);
	}

	public void removeChild(IConfigorNode node)
	{
		_Children.remove(node);
	}

	public LinkedList< IConfigorNode> getChildren()
	{
		return _Children;
	}

	private String _Name;
	private byte[] _Data;
	private IConfigorNode _Parent;
	private LinkedList< IConfigorNode> _Children;
}

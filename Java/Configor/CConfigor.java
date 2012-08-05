/*
	A little config thingy for C++ and C#
	Created by C0BRA
	Copyright XiaTek.org 2012
	Released under the MIT licence
*/

package Configor;

// ********* THIS CODE IS AUTO PORTED FROM C# TO JAVA USING CODEPORTING.COM TECHNOLOGY *********

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

import com.codeporting.csharp2java.java.Enum;
import com.codeporting.csharp2java.System.Text.Encoding;
import com.codeporting.csharp2java.System.Convert;
import com.codeporting.csharp2java.System.msString;
import com.codeporting.csharp2java.System.IO.MemoryStream;
import com.codeporting.csharp2java.System.IO.StreamReader;
import com.codeporting.csharp2java.System.IO.BinaryReader;
import com.codeporting.csharp2java.System.Text.msStringBuilder;
import com.codeporting.csharp2java.System.IO.StreamWriter;
//import com.codeporting.csharp2java.System.IO.File;
import com.codeporting.csharp2java.System.IO.TextWriter;
import com.codeporting.csharp2java.System.IO.SeekOrigin;


public class CConfigor
{
	static byte[] getBytes(String str)
	{
		return Encoding.UTF8.getBytes(str);
		//byte[] bytes = new byte[str.Length * sizeof(char)];
		//System.Buffer.BlockCopy(str.ToCharArray(), 0, bytes, 0, bytes.Length);
		//return bytes;
	}

	static String _GetString(byte[] bytes)
	{
		return Encoding.UTF8.getString(bytes);
		//char[] chars = new char[bytes.Length / sizeof(char)];
		//System.Buffer.BlockCopy(bytes, 0, chars, 0, bytes.Length);
		//return new string(chars);
	}

	public CConfigor()
	{
		_RootNode = new CConfigorNode(null, "root");
		_CurrentParseLine = 0;
		_Error = "";
	}

	public IConfigorNode get(String i)
	{
		IConfigorNode n = this._RootNode.getChild(i);
		if (n == null)
			return new CConfigorNode(this._RootNode, i);
		return n;
	}

	private LinkedList< CConfigorLexNode> lexify(String Input)
	{
		_CurrentParseLine = 1;
		MemoryStream s = new MemoryStream(getBytes(Input));
		_Reader = new StreamReader(s);
		_BReader = new BinaryReader(_Reader.getBaseStream(), Encoding.ASCII);

		LinkedList< CConfigorLexNode> ret = new LinkedList< CConfigorLexNode>();
		ENDLOOP:
		try {
			do
			{
				char x;
				try {
					x = _BReader.readChar();
				} catch (IOException e) {
					_Error = "Failed to read char of the binary reader!";
					return ret;
				}

				switch (x)
				{
					case '"':
						{
							String o = null;
							String[] referenceToO = { o };
							String err = parseQuotes(/*out*/ referenceToO);
							o = referenceToO[0];
							boolean resetfunc;
							if (err.length() > 1)
							{
								this._Error = err;
								CConfigorLexNode node = new CConfigorLexNode();
								node.Type = LexType.NONE;
								node.Error = err;
								ret.addLast(node);
								break ENDLOOP;
							}
							else
							{
								CConfigorLexNode node = new CConfigorLexNode();
								node.Type = LexType.STRING;
								node.Value = o;
								ret.addLast(node);
							}
						} break;
					case '{':
					case '}':
						{
							CConfigorLexNode node = new CConfigorLexNode();
							node.Type = LexType.TOKEN;
							node.Value = x;
							ret.addLast(node);
						} break;
					case '\n':
						{
							_CurrentParseLine++;
							CConfigorLexNode node = new CConfigorLexNode();
							node.Type = LexType.NEW_LINE;
							node.Value = x;
							ret.addLast(node);
						} break;
					default:
						break;
				}
			} while (_Reader.getBaseStream().getPosition() != _Reader.getBaseStream().getLength());
		} catch (IOException e) {
			_Error = "Failed to read from the stream";
		}
	//ENDLOOP:
		return ret;
	}

	private String parseQuotes(/*out*/ String[] Out)
	{
		Out[0] = null;
		StringBuilder sbj = new StringBuilder();
		msStringBuilder sb = new msStringBuilder();

		boolean literal_ = false;
		boolean hex_ = false;
		boolean DID_BREAK = false;
		EOL:
		try {
			do
			{
				char x;
				try {
					x = _BReader.readChar();
				} catch (IOException e) {
					Out[0] = "Failed to read from stream";
					return "";
				}
				if (hex_)
				{
					String hex;
					try {
						hex = com.codeporting.csharp2java.System.msString.concat(Character.toString(x),  Character.toString(_BReader.readChar()));
					} catch (IOException e) {
						Out[0] = "Failed to read from stream";
						return "";
					}
					int value = Convert.toInt32(hex, 16);
					
					sb.appendFormat(sbj, Character.toString(Convert.toChar(value)));

					hex_ = false;
					continue;
				}

				if (literal_)
				{
					switch (x)
					{
						case 't':
							sb.appendFormat(sbj, Character.toString('\t'));
							break;
						case 'n':
							sb.appendFormat(sbj, Character.toString('\n'));
							break;
						case 'r':
							sb.appendFormat(sbj, Character.toString('\r'));
							break;
						case 'b':
							sb.appendFormat(sbj, Character.toString('\b'));
							break;
						case 'x':
							hex_ = true;
							break;
						default:
							sb.appendFormat(sbj, Character.toString(x));
							break;
					}

					literal_ = false;
					continue;
				}
				else
				{
					switch (x)
					{
						case '\\':
							literal_ = true;
							break;
						case '"':
							DID_BREAK = true;
							break EOL;
						case '\r':
						case '\n':
							return msString.format("Expected '\"' near line {0}", _CurrentParseLine);
						default:
							sb.appendFormat(sbj, Character.toString(x));
							break;
					}
				}

			} while (_Reader.getBaseStream().getPosition() != _Reader.getBaseStream().getLength());
		} catch (IOException e) {
			Out[0] = "What happended here?";
			return "";
		}
		
		if(DID_BREAK == false)
			return msString.format("Expected '\"' near line {0}", _CurrentParseLine);

	//EOL:
		Out[0] = sbj.toString();
		return "";
	}

	public boolean loadFromFile(String Name)
	{
		StreamReader r;
		try
		{
			r = new StreamReader(Name);
			boolean ret = loadFromString(r.readToEnd());
			r.close();
			return ret;
		}
		catch(Exception e)
		{
			_Error = msString.format("Failed to open file \"{0}\"", Name);
			return false;
		}
	}

	public boolean loadFromString(String Input)
	{
		if (Input.length() == 0)
			return true;

		LinkedList< CConfigorLexNode> lexed = lexify(Input);

		if (_Error.length() != 0)
			return false;

		/*uint*/long Depth = 0;
		_CurrentParseLine = 1;

		IConfigorNode CurrentNode = getRootNode();
		IConfigorNode NewNode = null;

		//lexed.addLast((CConfigorLexNode)null); // So the loop functions correctly
		//for (LinkedListNode< CConfigorLexNode> it = lexed.First; it != lexed.Last; it = it.Next)
		ListIterator<CConfigorLexNode> it = lexed.listIterator();
		while(it.hasNext())
		{
			CConfigorLexNode node = it.next();

			switch (node.Type)
			{
				case LexType.NEW_LINE:
					_CurrentParseLine++;
					break;
				case LexType.NONE:
					_Error = node.Error;
					break;
				case LexType.STRING:
					{
						String name = (String)node.Value;
						IConfigorNode newnode = new CConfigorNode(CurrentNode, name);
						
						if(it.hasNext())
						{
							CConfigorLexNode it2 = it.next();
							
							if(it2.Type == LexType.STRING)
								newnode.setData(getBytes((String)it2.Value));
							else
								it.previous();
						}

						NewNode = newnode;
					} break;
				case LexType.TOKEN:
					{
						switch ((/*char*/Character)node.Value)
						{
							case '{':
								{
									if (NewNode == null)
									{
										_Error = msString.format("Node expected to group near line {0}", _CurrentParseLine);
										return false;
									}
									Depth++;
									CurrentNode = NewNode;
									NewNode = null;
								} break;
							case '}':
								{
									CurrentNode = CurrentNode.getParent();
									if (CurrentNode == null)
									{
										_Error = msString.format("EOF expected near line {0}", _CurrentParseLine);
										return false;
									}
									Depth--;
									NewNode = null;
								} break;
							default:
								_Error = msString.format("Unexpected token near line {0}", _CurrentParseLine);
								return false;
						}
					} break;
			}
		}
		if ((Depth & 0xFFFFFFFFL) != 0)
		{
			_Error = "Expected '}' near EOF";
			return false;
		}

		return true;
	}

	private String escapeData(String s)
	{
		msStringBuilder ret = new msStringBuilder();
		StringBuilder sbj = new StringBuilder();
		ret.ctor(s.length());

		for (int strLoop = 0; strLoop < s.length(); strLoop++)
		{
			char c = s.charAt(strLoop);
			byte x = (byte)c;
			if (((x & 0xFF) >= 32 && (x & 0xFF) <= 126) && c != '"' && c != '\\')
				ret.appendFormat(sbj, Character.toString(c));
			else
			{
				switch (c)
				{
					case '"':
						ret.appendFormat(sbj, "\\\"");
						break;
					case '\n':
						ret.appendFormat(sbj, "\\n");
						break;
					case '\r':
						ret.appendFormat(sbj, "\\r");
						break;
					case '\t':
						ret.appendFormat(sbj, "\\t");
						break;
					case '\b':
						ret.appendFormat(sbj, "\\b");
						break;
					case '\\':
						ret.appendFormat(sbj, "\\\\");
						break;
					default:
						{
							ret.appendFormat(sbj, "\\x{0:x2}", x);
						} break;
				}
			}
		}

		return sbj.toString();
	}

	private void doDepth(StringBuilder w, int Depth) throws IOException
	{
		for (int i = 0; i < Depth; i++)
			w.append("\t");
	}

//Auto generated code to handle default paramters

	private void recursiveSave(StringBuilder w, IConfigorNode node) throws IOException
{
 int Depth =  0;
 recursiveSave( w, node, Depth);
}private void recursiveSave(StringBuilder w ,IConfigorNode node, int Depth) throws IOException
	{
		doDepth(w, Depth);

		// Name
		w.append("\"");
		w.append(escapeData(node.getName()));
		w.append("\"");
		
		// Data
		if (node.getData() != null && node.getData().length != 0)
		{
			w.append(" \"");
			w.append(escapeData(node.getString()));
			w.append("\"");
		}

		w.append('\n');

		if (node.getChildren().size() > 0)
		{
			doDepth(w, Depth);
			w.append("{\n");

			for (IConfigorNode child : (Iterable<IConfigorNode>) node.getChildren())
				recursiveSave(w, child, Depth + 1);

			doDepth(w, Depth);
			w.append("}\n");
		}
	}

	public boolean saveToFile(String Name)
	{
		File f;
		try
		{
			f = new File(Name.replace('\\', '/'));
			if(!f.exists())
				f.createNewFile();
			
		}
		catch(Exception e) { return false; }

		LinkedList< IConfigorNode> lst = getRootNode().getChildren();
		
		StringBuilder sb = new StringBuilder();
		
		for (IConfigorNode n : (Iterable<IConfigorNode>) lst)
			try 
			{
				recursiveSave(sb, n);
			} catch (IOException e) 
			{
				return false;
			}

		try 
		{
			FileWriter w = new FileWriter(f);
			w.write(sb.toString());
			w.flush();
			return true;
		} catch (Exception e) 
		{
			return false;
		}
	}

	public /*override*/ String toString()
	{
		String ret;
		try
		{
			StringBuilder sb = new StringBuilder();
			
			LinkedList< IConfigorNode> lst = getRootNode().getChildren();
	
			for (IConfigorNode n : (Iterable<IConfigorNode>) lst)
				recursiveSave(sb, n);
	
			ret = sb.toString();
		}
		catch(Exception ex)
		{
			return "";
		}
		return ret;
	}

	public IConfigorNode getRootNode()
	{
		return _RootNode;
	}

	public String getError()
	{
		return _Error;
	}

	private IConfigorNode _RootNode;
	private /*uint*/long _CurrentParseLine;
	private StreamReader _Reader;
	private BinaryReader _BReader;
	private String _Error;
}


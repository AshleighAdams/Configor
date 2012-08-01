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

public class CConfigorNode implements IConfigorNode
{
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

	public <T> T getValue(T Default)
	{
		if (_Data == null)
		{
			this.setValue<T>(Default);
			return Default;
		}

		return (T)Convert.changeType(getString(), ms.typeOf(T.class));
	}

	public <T> void setValue(T Default)
	{
		this.setString(Default.toString());
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
		_Children.AddLast(node);
	}

	public void removeChild(IConfigorNode node)
	{
		_Children.Remove(node);
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

class CConfigorLexNode
{
	public /*LexType*/int Type;
	public Object Value;
	public String Error;
}

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
		_BReader = new BinaryReader(_Reader.getBaseStream());

		LinkedList< CConfigorLexNode> ret = new LinkedList< CConfigorLexNode>();
		ENDLOOP:
		do
		{
			char x = _BReader.readChar();

			switch (x)
			{
				case '"':
					{
						String o = null;
						String[] referenceToO = { o };
						String err = parseQuotes(/*out*/ referenceToO);
						o = referenceToO[0];
						if (err.length() > 1)
						{
							this._Error = err;
							CConfigorLexNode node = new CConfigorLexNode();
							node.Type = LexType.NONE;
							node.Error = err;
							ret.AddLast(node);
							//goto ENDLOOP;
							break ENDLOOP;
						}
						else
						{
							CConfigorLexNode node = new CConfigorLexNode();
							node.Type = LexType.STRING;
							node.Value = o;
							ret.AddLast(node);
						}
					} break;
				case '{':
				case '}':
					{
						CConfigorLexNode node = new CConfigorLexNode();
						node.Type = LexType.TOKEN;
						node.Value = x;
						ret.AddLast(node);
					} break;
				case '\n':
					{
						_CurrentParseLine++;
						CConfigorLexNode node = new CConfigorLexNode();
						node.Type = LexType.NEW_LINE;
						node.Value = x;
						ret.AddLast(node);
					} break;
				default:
					break;
			}
		} while (_Reader.getBaseStream().getPosition() != _Reader.getBaseStream().getLength());
	//ENDLOOP:
		return ret;
	}

	private String parseQuotes(/*out*/ String[] Out)
	{
		Out[0] = null;
		msStringBuilder sb = msStringBuilder.ctor();

		boolean literal_ = false;
		boolean hex_ = false;
		
		EOL:
		do
		{
			char x = _BReader.readChar();
			if (hex_)
			{
				String hex = com.codeporting.csharp2java.System.msString.concat(Character.toString(x),  Character.toString(_BReader.readChar()));
				int value = Convert.toInt32(hex, 16);
				sb.append(char.convertFromUtf32(value));

				hex_ = false;
				continue;
			}

			if (literal_)
			{
				switch (x)
				{
					case 't':
						sb.append('\t');
						break;
					case 'n':
						sb.append('\n');
						break;
					case 'r':
						sb.append('\r');
						break;
					case 'b':
						sb.append('\b');
						break;
					case 'x':
						hex_ = true;
						break;
					default:
						sb.append(x);
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
						break EOL;
						//goto EOL;
					case '\r':
					case '\n':
						return msString.format("Expected '\"' near line {0}", _CurrentParseLine);
					default:
						sb.append(x);
						break;
				}
			}

		} while (_Reader.getBaseStream().getPosition() != _Reader.getBaseStream().getLength());

		return msString.format("Expected '\"' near line {0}", _CurrentParseLine);

	//EOL:
		Out[0] = sb.toString();
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

		StreamWriter app = File.appendText("lexed.txt");
		app.writeLine("====================");
		for (CConfigorLexNode node : (Iterable<CConfigorLexNode>) lexed)
		{
			if (node.Value != null)
				app.writeLine("{0} {1}", node.Type.toString(), node.Value.toString());
		}
		app.close();

		if (_Error.length() != 0)
			return false;

		/*uint*/long Depth = 0;
		_CurrentParseLine = 1;

		IConfigorNode CurrentNode = getRootNode();
		IConfigorNode NewNode = null;

		lexed.AddLast((CConfigorLexNode)null); // So the loop functions correctly
		for (LinkedListNode< CConfigorLexNode> it = lexed.First; it != lexed.Last; it = it.Next)
		{
			CConfigorLexNode node = it.Value;

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
						LinkedListNode< CConfigorLexNode> it2 = it.Next;

						String name = (String)node.Value;
						IConfigorNode newnode = new CConfigorNode(CurrentNode, name);

						if (it2 != lexed.Last && it2.Value.Type == LexType.STRING)
						{
							it = it2;
							newnode.setData(getBytes((String)it2.Value.Value));
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
		msStringBuilder ret = msStringBuilder.ctor(s.length());

		for (int strLoop = 0; strLoop < s.length(); strLoop++)
		{
			char c = s.charAt(strLoop);
			byte x = (byte)c;
			if (((x & 0xFF) >= 32 && (x & 0xFF) <= 126) && c != '"' && c != '\\')
				ret.append(c);
			else
			{
				switch (c)
				{
					case '"':
						ret.append("\\\"");
						break;
					case '\n':
						ret.append("\\n");
						break;
					case '\r':
						ret.append("\\r");
						break;
					case '\t':
						ret.append("\\t");
						break;
					case '\b':
						ret.append("\\b");
						break;
					case '\\':
						ret.append("\\\\");
						break;
					default:
						{
							msStringBuilder.appendFormat(ret, "\\x{0:x2}", x);
						} break;
				}
			}
		}

		return ret.toString();
	}

	private void doDepth(StreamWriter w, int Depth)
	{
		for (int i = 0; i < Depth; i++)
			w.write("\t");
	}

//Auto generated code to handle default paramters

	private void recursiveSave(StreamWriter w, IConfigorNode node)
 {
 int Depth =  0;
 recursiveSave( w, node, Depth);
}private void recursiveSave(StreamWriter w,IConfigorNode node, int Depth)
	{
		doDepth(w, Depth);

		w.write("\"{0}\"", escapeData(node.getName())); // Name

		if (node.getData() != null && node.getData().length != 0)
			w.write(" \"{0}\"", escapeData(node.getString()));

		w.write('\n');

		if (node.getChildren().Count > 0)
		{
			doDepth(w, Depth);
			w.write("{\n");

			for (IConfigorNode child : (Iterable<IConfigorNode>) node.getChildren())
				recursiveSave(w, child, Depth + 1);

			doDepth(w, Depth);
			w.write("}\n");
		}
	}

	public boolean saveToFile(String Name)
	{
		StreamWriter w;
		try
		{
			w = new StreamWriter(Name);
		}
		catch(Exception e) { return false; }

		LinkedList< IConfigorNode> lst = getRootNode().getChildren();

		for (IConfigorNode n : (Iterable<IConfigorNode>) lst)
			recursiveSave(w, n);

		w.flush();

		return true;
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


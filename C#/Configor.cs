/*
	A little config thingy for C++ and C#
	Created by C0BRA
	Copyright XiaTek.org 2012
	Released under the MIT licence
*/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using System.IO;

namespace Configor
{
	public interface IConfigorNode
	{
		string GetName();
		byte[] GetData();
		void SetData(byte[] pData);
		void SetString(string Data);
		string GetString();
		T GetValue<T>(T Default);
		void SetValue<T>(T Value);
		IConfigorNode GetChild(string Name);
		IConfigorNode GetParent();
		void AddChild(IConfigorNode Node);
		void RemoveChild(IConfigorNode Node);
		LinkedList<IConfigorNode> GetChildren();
		IConfigorNode this[string i] { get; }
	}

	public class CConfigorNode : IConfigorNode
	{
		static byte[] _GetBytes(string str)
		{
			return Encoding.UTF8.GetBytes(str);
			//byte[] bytes = new byte[str.Length * sizeof(char)];
			//System.Buffer.BlockCopy(str.ToCharArray(), 0, bytes, 0, bytes.Length);
			//return bytes;
		}

		static string _GetString(byte[] bytes)
		{
			if (bytes == null)
				return "";
			return Encoding.UTF8.GetString(bytes);
			//if (bytes == null)
			//	return "";
			//char[] chars = new char[bytes.Length / sizeof(char)];
			//System.Buffer.BlockCopy(bytes, 0, chars, 0, bytes.Length);
			//return new string(chars);
		}

		public CConfigorNode(IConfigorNode Parent, string Name)
		{
			_Data = null;
			_Children = new LinkedList<IConfigorNode>();
			_Name = Name;
			_Parent = Parent;
			if (_Parent != null)
				_Parent.AddChild(this);
		}


		public IConfigorNode this[string i]
		{
			get
			{
				IConfigorNode n = this.GetChild(i);
				if (n == null)
					return new CConfigorNode(this, i);
				return n;
			}
			//set {  }
		}

		public string GetName()
		{
			return _Name;
		}

		public byte[] GetData()
		{
			return _Data;
		}

		public void SetData(byte[] Data)
		{
			_Data = Data;
		}

		public void SetString(string Data)
		{
			_Data = _GetBytes(Data);
		}

		public string GetString()
		{
			return _GetString(_Data);
		}

		public T GetValue<T>(T Default)
		{
			if (_Data == null)
			{
				this.SetValue<T>(Default);
				return Default;
			}

			return (T)Convert.ChangeType(GetString(), typeof(T));
		}

		public void SetValue<T>(T Default)
		{
			this.SetString(Default.ToString());
		}

		public IConfigorNode GetChild(string i)
		{
			foreach (IConfigorNode node in _Children)
				if (node.GetName() == i)
					return node;
			return null;
		}

		public IConfigorNode GetParent()
		{
			return _Parent;
		}

		public void AddChild(IConfigorNode node)
		{
			_Children.AddLast(node);
		}

		public void RemoveChild(IConfigorNode node)
		{
			_Children.Remove(node);
		}

		public LinkedList<IConfigorNode> GetChildren()
		{
			return _Children;
		}

		string _Name;
		byte[] _Data;
		IConfigorNode _Parent;
		LinkedList<IConfigorNode> _Children;
	}

	public enum LexType
	{
		None,
		Token = 1,
		String = 2,
		NewLine = 3
	}

	class CConfigorLexNode
	{
		public LexType Type;
		public object Value;
		public string Error;
	}

	public class CConfigor
	{
		static byte[] GetBytes(string str)
		{
			return Encoding.UTF8.GetBytes(str);
			//byte[] bytes = new byte[str.Length * sizeof(char)];
			//System.Buffer.BlockCopy(str.ToCharArray(), 0, bytes, 0, bytes.Length);
			//return bytes;
		}

		static string _GetString(byte[] bytes)
		{
			return Encoding.UTF8.GetString(bytes);
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

		public IConfigorNode this[string i]
		{
			get
			{
				IConfigorNode n = this._RootNode.GetChild(i);
				if (n == null)
					return new CConfigorNode(this._RootNode, i);
				return n;
			}
			//set {  }
		}

		LinkedList<CConfigorLexNode> Lexify(string Input)
		{
			_CurrentParseLine = 1;
			MemoryStream s = new MemoryStream(GetBytes(Input));
			_Reader = new StreamReader(s);
			_BReader = new BinaryReader(_Reader.BaseStream);

			LinkedList<CConfigorLexNode> ret = new LinkedList<CConfigorLexNode>();

			do
			{
				char x = _BReader.ReadChar();

				switch (x)
				{
					case '"':
						{
							string o;
							string err = ParseQuotes(out o);
							if (err.Length > 1)
							{
								this._Error = err;
								CConfigorLexNode node = new CConfigorLexNode();
								node.Type = LexType.None;
								node.Error = err;
								ret.AddLast(node);
								goto ENDLOOP;
							}
							else
							{
								CConfigorLexNode node = new CConfigorLexNode();
								node.Type = LexType.String;
								node.Value = o;
								ret.AddLast(node);
							}
						} break;
					case '{':
					case '}':
						{
							CConfigorLexNode node = new CConfigorLexNode();
							node.Type = LexType.Token;
							node.Value = x;
							ret.AddLast(node);
						} break;
					case '\n':
						{
							_CurrentParseLine++;
							CConfigorLexNode node = new CConfigorLexNode();
							node.Type = LexType.NewLine;
							node.Value = x;
							ret.AddLast(node);
						} break;
					default:
						break;
				}
			} while (_Reader.BaseStream.Position != _Reader.BaseStream.Length);
		ENDLOOP:
			return ret;
		}

		string ParseQuotes(out string Out)
		{
			Out = null;
			StringBuilder sb = new StringBuilder();

			bool literal_ = false;
			bool hex_ = false;

			do
			{
				char x = _BReader.ReadChar();
				if (hex_)
				{
					string hex = x.ToString() + _BReader.ReadChar().ToString();
					int value = Convert.ToInt32(hex, 16);
					sb.Append(Char.ConvertFromUtf32(value));

					hex_ = false;
					continue;
				}

				if (literal_)
				{
					switch (x)
					{
						case 't':
							sb.Append('\t');
							break;
						case 'n':
							sb.Append('\n');
							break;
						case 'r':
							sb.Append('\r');
							break;
						case 'b':
							sb.Append('\b');
							break;
						case 'x':
							hex_ = true;
							break;
						default:
							sb.Append(x);
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
							goto EOL;
						case '\r':
						case '\n':
							return string.Format("Expected '\"' near line {0}", _CurrentParseLine);
						default:
							sb.Append(x);
							break;
					}
				}

			} while (_Reader.BaseStream.Position != _Reader.BaseStream.Length);

			return string.Format("Expected '\"' near line {0}", _CurrentParseLine);

		EOL:
			Out = sb.ToString();
			return "";
		}

		public bool LoadFromFile(string Name)
		{
			StreamReader r;
			try
			{
				r = new StreamReader(Name);
				bool ret = LoadFromString(r.ReadToEnd());
				r.Close();
				return ret;
			}
			catch
			{
				_Error = string.Format("Failed to open file \"{0}\"", Name);
				return false;
			}
		}

		public bool LoadFromString(string Input)
		{
			if (Input.Length == 0)
				return true;

			LinkedList<CConfigorLexNode> lexed = Lexify(Input);

			StreamWriter app = File.AppendText("lexed.txt");
			app.WriteLine("====================");
			foreach (CConfigorLexNode node in lexed)
			{
				if (node.Value != null)
					app.WriteLine("{0} {1}", node.Type.ToString(), node.Value.ToString());
			}
			app.Close();

			if (_Error.Length != 0)
				return false;

			uint Depth = 0;
			_CurrentParseLine = 1;

			IConfigorNode CurrentNode = GetRootNode();
			IConfigorNode NewNode = null;

			lexed.AddLast((CConfigorLexNode)null); // So the loop functions correctly
			for (LinkedListNode<CConfigorLexNode> it = lexed.First; it != lexed.Last; it = it.Next)
			{
				CConfigorLexNode node = it.Value;

				switch (node.Type)
				{
					case LexType.NewLine:
						_CurrentParseLine++;
						break;
					case LexType.None:
						_Error = node.Error;
						break;
					case LexType.String:
						{
							LinkedListNode<CConfigorLexNode> it2 = it.Next;

							string name = (string)node.Value;
							IConfigorNode newnode = new CConfigorNode(CurrentNode, name);

							if (it2 != lexed.Last && it2.Value.Type == LexType.String)
							{
								it = it2;
								newnode.SetData(GetBytes((string)it2.Value.Value));
							}

							NewNode = newnode;
						} break;
					case LexType.Token:
						{
							switch ((char)node.Value)
							{
								case '{':
									{
										if (NewNode == null)
										{
											_Error = string.Format("Node expected to group near line {0}", _CurrentParseLine);
											return false;
										}
										Depth++;
										CurrentNode = NewNode;
										NewNode = null;
									} break;
								case '}':
									{
										CurrentNode = CurrentNode.GetParent();
										if (CurrentNode == null)
										{
											_Error = string.Format("EOF expected near line {0}", _CurrentParseLine);
											return false;
										}
										Depth--;
										NewNode = null;
									} break;
								default:
									_Error = string.Format("Unexpected token near line {0}", _CurrentParseLine);
									return false;
							}
						} break;
				}
			}
			if (Depth != 0)
			{
				_Error = "Expected '}' near EOF";
				return false;
			}

			return true;
		}

		string EscapeData(string s)
		{
			StringBuilder ret = new StringBuilder(s.Length);

			foreach (char c in s)
			{
				byte x = (byte)c;
				if ((x >= 32 && x <= 126) && c != '"' && c != '\\')
					ret.Append(c);
				else
				{
					switch (c)
					{
						case '"':
							ret.Append("\\\"");
							break;
						case '\n':
							ret.Append("\\n");
							break;
						case '\r':
							ret.Append("\\r");
							break;
						case '\t':
							ret.Append("\\t");
							break;
						case '\b':
							ret.Append("\\b");
							break;
						case '\\':
							ret.Append("\\\\");
							break;
						default:
							{
								ret.AppendFormat("\\x{0:x2}", x);
							} break;
					}
				}
			}

			return ret.ToString();
		}

		void DoDepth(TextWriter w, int Depth)
		{
			for (int i = 0; i < Depth; i++)
				w.Write("\t");
		}

		void RecursiveSave(TextWriter w, IConfigorNode node, int Depth = 0)
		{
			DoDepth(w, Depth);

			w.Write("\"{0}\"", EscapeData(node.GetName())); // Name

			if (node.GetData() != null && node.GetData().Length != 0)
				w.Write(" \"{0}\"", EscapeData(node.GetString()));

			w.Write('\n');

			if (node.GetChildren().Count > 0)
			{
				DoDepth(w, Depth);
				w.Write("{\n");

				foreach (IConfigorNode child in node.GetChildren())
					RecursiveSave(w, child, Depth + 1);

				DoDepth(w, Depth);
				w.Write("}\n");
			}
		}

		public bool SaveToFile(string Name)
		{
			StreamWriter w;
			try
			{
				w = new StreamWriter(Name);
			}
			catch { return false; }

			LinkedList<IConfigorNode> lst = GetRootNode().GetChildren();

			foreach (IConfigorNode n in lst)
				RecursiveSave(w, n);

			w.Flush();

			return true;
		}

		public override string ToString()
		{
			MemoryStream ms = new MemoryStream();
			TextWriter w = new StreamWriter(ms);
			StreamReader r = new StreamReader(ms);
			
			LinkedList<IConfigorNode> lst = GetRootNode().GetChildren();

			foreach (IConfigorNode n in lst)
				RecursiveSave(w, n);

			ms.Seek(0, SeekOrigin.Begin);
			string ret = r.ReadToEnd();

			w.Close();
			r.Close();
			ms.Close();

			return ret;
		}

		public IConfigorNode GetRootNode()
		{
			return _RootNode;
		}

		public string GetError()
		{
			return _Error;
		}

		IConfigorNode _RootNode;
		uint _CurrentParseLine;
		StreamReader _Reader;
		BinaryReader _BReader;
		string _Error;
	}
}

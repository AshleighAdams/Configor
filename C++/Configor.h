/*
	A little config thingy for C++, C#, Java, and Lua
	Created by C0BRA
	Copyright XiaTek.org 2013
	Released under the GPLv2 licence
*/

#ifndef CONFIGOR_H
#define CONFIGOR_H

#include <string>
#include <list>
#include <sstream>
#include <locale>

class IConfigorNode;
class CConfigor;
typedef std::list<IConfigorNode*> IConfigorNodeList;

class IConfigorNode
{
public:
	virtual ~IConfigorNode() {};
	virtual std::string GetName() = 0;
	virtual size_t GetDataLength() = 0;
	virtual unsigned char* GetData() = 0;
	virtual void SetData(unsigned char* pData, size_t Length) = 0;
	virtual std::string GetString(const std::string& Default = "") = 0;
	virtual void SetString(const std::string& Value) = 0;
	virtual IConfigorNode* GetChild(std::string Name) = 0;
	virtual IConfigorNode* GetParent() = 0;
	virtual void AddChild(IConfigorNode* Node) = 0;
	virtual void RemoveChild(IConfigorNode* Node) = 0;
	virtual IConfigorNodeList* GetChildren() = 0;
	virtual IConfigorNode& operator[](const std::string& key) = 0;
	virtual void operator=(char* pData) = 0;

	// Ease of use functions
	template<typename T> T GetValue(const T Default);
	template<typename T> void SetValue(const T Value);
};

typedef std::list<IConfigorNode*> IConfigorNodeList;

class CConfigorNode : public IConfigorNode
{
protected:
	IConfigorNodeList m_Children;
	IConfigorNode* m_pParent;

	size_t m_Length;
	unsigned char* m_pData;

	void DestroyData();

	std::string m_Name;

public:
	CConfigorNode(IConfigorNode* pParent, std::string Name);
	~CConfigorNode();

	std::string GetName();
	size_t GetDataLength();
	unsigned char* GetData();
	void SetData(unsigned char* pData, size_t Length);
	std::string GetString(const std::string& Default);
	void SetString(const std::string& Value);
	IConfigorNode* GetChild(std::string Name);
	IConfigorNode* GetParent();
	void AddChild(IConfigorNode* Node);
	void RemoveChild(IConfigorNode* Node);
	IConfigorNodeList* GetChildren();
	IConfigorNode& operator[](const std::string& key);
	void operator=(char* pData);
};

namespace ConfigorLexType
{
	const extern int None;
	const extern int Token;
	const extern int String;
	const extern int NewLine; // for line counting only
}

struct CConfigorLexNode
{
	int Type;
	void* Value;
	size_t Length;
	std::string Error;
};

typedef std::list<CConfigorLexNode*> CConfigorLexNodeList;

class CConfigor
{
	IConfigorNode* m_pRootNode;
	size_t m_CurrentParseLine;
	unsigned char* m_pCurrentParseChar;

	std::string m_Error;

	// will be copied
	CConfigorLexNodeList Lexify(char* Input, size_t Length);
	std::string ParseQuotes(char** Out, size_t* Length, char* End);
public:
	CConfigor();
	~CConfigor();
	bool LoadFromFile(const std::string& Name);
	bool LoadFromString(char* Input, size_t Length);
	bool SaveToFile(const std::string& Name);
	std::string ToString();
	IConfigorNode* GetRootNode();
	std::string GetError();
	IConfigorNode& operator[](const std::string& Index);
};

template<typename T> T IConfigorNode::GetValue(const T Default) // Sets the nodes value to the default if it doesn't exist too
{
	if(!GetData())
	{
		SetValue<T>(Default);
		return Default;
	}
	
	try
	{
		T ret = T();
		std::stringstream ss;
		ss.imbue(std::locale(""));
		ss << GetString();
		ss >> ret;

		return ret;
	}
	catch(...)
	{
		SetValue<T>(Default);
		return Default;
	}
}

template<typename T> void IConfigorNode::SetValue(const T Value)
{
	SetData(0, 0); // This should simulate destroying data

	std::string val;
	std::stringstream ss;
	ss.imbue(std::locale(""));
	
	ss << Value;
	ss >> val;

	SetString(val.c_str());
}

#endif

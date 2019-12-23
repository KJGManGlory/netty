Java IO流分类
- 功能：输入输出流
- 结构：字节流，字符流
字节流：InputStream, OutputStream
字符流：Reader, Writer

节点流：从特定地方进行读写的流
过滤流：基于节点流进行了包装处理

读操作流程：
1. open a stream
2. while more information
3. read information
4. close stream

写操作流程：
1. open a stream
2. while more information
3. write information
4. close stream

装饰器模式（对对象进行功能扩展，而继承是对对象进行的扩展）：
意图作用
- 装饰器模式又称为包装模式(wrapper)
- 装饰器模式在不创建更多子类的情况下，使对象的功能加以扩展
角色
- 抽象构件角色：
- 具体构件角色：
- 装饰角色：
- 具体装饰角色：
装饰模式VS继承
 
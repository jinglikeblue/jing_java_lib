
package core.events;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * 事件广播者
 * 
 * @author Jing
 */
public class EventDispatcher
{

	private HashMap<String, Vector<IEventListener>> _eventMap = new HashMap<String, Vector<IEventListener>>();

	public EventDispatcher()
	{
	}

	/**
	 * 广播事件
	 * 
	 * @param type
	 * @param data
	 */
	public void dispatchEvent(String type, Object data)
	{
		Vector<IEventListener> listeners = _eventMap.get(type);
		if(null == listeners)
		{
			return;
		}

		Iterator<IEventListener> it = listeners.iterator();

		while(it.hasNext())
		{
			it.next().onReciveEvent(type, this, data);
		}
	}

	/**
	 * 监听指定的事件
	 * 
	 * @param type
	 * @param listener
	 */
	public void addEventListener(String type, IEventListener listener)
	{
		Vector<IEventListener> listeners = _eventMap.get(type);
		if(null == listeners)
		{
			listeners = new Vector<IEventListener>();
			_eventMap.put(type, listeners);
		}

		if(false == listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}

	/**
	 * 移除对指定事件的监听
	 * 
	 * @param type
	 * @param listener
	 */
	public void removeEventListener(String type, IEventListener listener)
	{
		Vector<IEventListener> listeners = _eventMap.get(type);
		if(null != listeners)
		{
			listeners.remove(listener);
		}
	}

}

package com.zsw.simpletomcat;

/**
 * Valve可以通过该接口至多与一个Servlet容器关联
 * @author zsw
 * @descirption
 * @date 2020/07/23 18:01
 */
public interface Contained {

	public Container getContainer();

	public void setContainer(Container container);
}

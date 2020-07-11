package com.zsw.simpletomcat.enums;

import java.util.stream.Stream;

/**
 * @author zsw
 * @date 2020/07/11 13:37
 */
public enum HttpStatusEnum {

	/* 请求成功 */
	OK(200,"OK"),
	/* 不存在 */
	NOT_FOUND(404,"FileNotFound")
	;
	private Integer status;
	private String desc;

	HttpStatusEnum(Integer status, String desc) {
		this.status = status;
		this.desc = desc;
	}

	public static HttpStatusEnum valueOf(Integer value) {
		return Stream.of(values()).filter(status->status.getStatus().equals(value)).findAny().orElse(null);
	}

	public Integer getStatus() {
		return status;
	}

	public String getDesc() {
		return desc;
	}
}

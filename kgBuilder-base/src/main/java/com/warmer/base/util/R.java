package com.warmer.base.util;

import com.warmer.base.enums.ReturnStatus;
import lombok.Data;

/**
 * 接口统一返回结果
 *
 * 推荐使用
 * <p>成功：R.success() </p>
 * <p>成功：R.success(data) </p>
 * <p>成功：R.success(data,msg)</p>
 * <p>出错：R.error()</p>
 * <p>出错：R.error(msg)</p>
 * <p>验证型错误：R.warn(msg)</p>
 * <p>定义参数的错误：R.create(returnStatus)</p>
 * @param <T>
 */
@Data
public class R<T> {
	public int code;
	public T data;
	public String msg;

	/**
	 * 参数验证失败
	 *
	 * @param msg
	 * @return
	 */
	public static R warn(String msg) {
		return new R(ReturnStatus.ValidateFailure.getValue(), msg, "");
	}

	/**
	 * 创建实例
	 *
	 * @return
	 */
	public static R create(ReturnStatus returnStatus) {
		return new R(returnStatus.getValue(), returnStatus.getName(), "");
	}

	/**
	 * 创建实例
	 *
	 * @return
	 */
	public static R create(ReturnStatus returnStatus, String msg) {
		return new R(returnStatus.getValue(), msg, "");
	}

	/**
	 * 出错
	 *
	 * @param msg
	 * @return
	 */
	public static R error(String msg) {
		return new R(ReturnStatus.Error.getValue(), msg, "");
	}

	/**
	 * 出错
	 *
	 * @return
	 */
	public static R error() {
		return new R(ReturnStatus.Error.getValue(), ReturnStatus.Error.getName(), "");
	}

	/**
	 * 成功
	 *
	 * @param <T>
	 * @return
	 */
	public static <T> R success() {
		return new R(ReturnStatus.Success.getValue(), "", "");
	}

	/**
	 * 成功
	 *
	 * @param data
	 * @param <T>
	 * @return
	 */
	public static <T> R success(T data) {
		return new R(ReturnStatus.Success.getValue(), "", data);
	}
	/**
	 * 成功
	 *
	 * @param msg
	 * @param <T>
	 * @return
	 */
	public static <T> R success(String msg) {
		return new R(ReturnStatus.Success.getValue(), msg, null);
	}
	/**
	 * 成功
	 *
	 * @param data
	 * @param <T>
	 * @return
	 */
	public static <T> R success(T data, String msg) {
		return new R(ReturnStatus.Success.getValue(), msg, data);
	}


	/**
	 * 私有构建方法，请使用静态方法构建实例
	 */
	private R(int code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
}

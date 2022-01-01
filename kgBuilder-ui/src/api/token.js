/**
 * @description: token操作
 */

export const TOKENKEY = "authtoken";

// 存储token
export const setToken = (token, key) => {
  localStorage.setItem(key || TOKENKEY, token);
};

// 获取token
export const getToken = key => localStorage.getItem(key || TOKENKEY) || null;

// 删除token
export const removeToken = key => {
  localStorage.removeItem(key || TOKENKEY);
};

// 获取cookie
export const getCookie = name => {
  var arr;
  var reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
  if ((arr = document.cookie.match(reg))) return +unescape(arr[2]);
  else return null;
};

// 移除所有
export const removeAll = () => {
  localStorage.clear();
};

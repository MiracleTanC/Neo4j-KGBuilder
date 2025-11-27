import Cookies from "js-cookie";

const TokenKey = "Admin-Token";

/**
 * 获取 Token
 * @returns {string|undefined} 当前 Token 值
 */
export function getToken() {
  return Cookies.get(TokenKey);
}

/**
 * 设置 Token
 * @param {string} token Token 字符串
 * @returns {string} 设置后的 Token
 */
export function setToken(token) {
  return Cookies.set(TokenKey, token);
}

/**
 * 移除 Token
 * @returns {void}
 */
export function removeToken() {
  return Cookies.remove(TokenKey);
}

export const getToken = () => localStorage.getItem('Authorization');
export const setToken = (token) => localStorage.setItem('Authorization', token);
export const removeToken = () => localStorage.removeItem('Authorization');
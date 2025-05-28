export const getToken = () => localStorage.getItem('Authorization');

export const setToken = (res) => {
    const token = res.headers['authorization'];

    localStorage.setItem('Authorization', token);
}

export const removeToken = () => localStorage.removeItem('Authorization');
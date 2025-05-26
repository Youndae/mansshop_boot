export const getToken = () => {
    const tokenObject = JSON.parse(localStorage.getItem('Authorization') || '{}');

    if(tokenObject){
        if(new Date(tokenObject.expires) < new Date()){
            console.log('token expire');
            removeToken();
            return undefined;
        }else {
            return tokenObject.token;
        }
    }

    return undefined;
}

export const setToken = (res) => {
    const auth = {
        token: res.headers['authorization'],
        expires: new Date(res.data.tokenExpiration.expiration).toISOString()
    }

    localStorage.setItem('Authorization', JSON.stringify(auth));
}
export const removeToken = () => localStorage.removeItem('Authorization');
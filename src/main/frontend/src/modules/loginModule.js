import {setMember} from "./member";

export function handleLocationPathToLogin(pathname, navigate) {
    if(pathname === '/login' || pathname === '/join')
        pathname = '/';

    navigate('/login', { state: pathname });
}

export function setMemberObject(res, loginState) {
    const userStatus = res.data.userStatus;

    if(loginState !== userStatus.loggedIn)
        return setMember(userStatus);


    return undefined;
}
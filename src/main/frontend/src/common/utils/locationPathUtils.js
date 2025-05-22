export function handleLocationPathToLogin(pathname, navigate) {
    if(pathname === '/login' || pathname === '/join')
        pathname = '/';

    navigate('/login', { state: pathname });
}
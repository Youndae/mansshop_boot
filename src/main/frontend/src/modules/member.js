//reducer
export default function user(state = 'default', action) {
    switch (action.type) {
        case 'isLoggedIn' :
            return state = 'loggedIn';
        case 'isLoggedOut' :
            return state = 'loggedOut';
        default:
            return state = 'default';
    }
}
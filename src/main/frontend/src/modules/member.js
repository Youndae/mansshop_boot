//reducer
export const setMember = (status) => {
    let type = 'isLoggedOut';

    if(status.loggedIn)
        type = 'isLoggedIn';

    return {
        type: type,
        status: status.loggedIn,
        id: status.uid,
    }
}


export default function member(state, action) {
    switch (action.type) {
        case 'isLoggedIn' :
            return { ...state, loginStatus: action.status, id: action.id };
        case 'isLoggedOut' :
            return { ...state, loginStatus: action.status, id: action.id };
        default :
            return { ...state, loginStatus: false, id: null };
    }
}
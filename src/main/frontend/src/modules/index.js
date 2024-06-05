import { legacy_createStore as createStore, combineReducers } from "redux";
import { persistReducer } from "redux-persist";
import storage from 'redux-persist/lib/storage';

import member from './member';

/*const rootReducer = combineReducers({
    member,
})*/

const persistConfig = {
    key: 'root',
    storage: storage
};


const rootReducer = combineReducers({
    member,
})

const store = createStore(persistReducer(persistConfig, rootReducer),
    window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());

// export default rootReducer;

export default store;
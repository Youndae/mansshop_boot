import {combineReducers, configureStore} from '@reduxjs/toolkit';
import { persistStore, persistReducer} from "redux-persist";
import storage from 'redux-persist/lib/storage';
import memberReducer from '../modules/member/memberSlice';

const rootReducer = combineReducers({
    member: memberReducer,
});
const persistConfig = {
    key: 'root',
    storage,
    blacklist: ['member'],
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = configureStore({
    reducer: persistedReducer,
    devTools: process.env.NODE_ENV !== 'production',
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: {
                ignoreActions: ['persist/PERSIST', 'persist/REHYDRATE'],
            },
        }),
});

export const persistor = persistStore(store);
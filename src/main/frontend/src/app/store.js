import { configureStore } from '@reduxjs/toolkit';
import { persistStore, persistReducer} from "redux-persist";
import storage from 'redux-persist/lib/storage';
import rootReducer from './rootReducer';

const persistConfig = {
    key: 'root',
    storage,
    whitelist: ['member'],
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
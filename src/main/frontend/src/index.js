import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { Provider } from 'react-redux';
import { CookiesProvider } from "react-cookie";
import { PersistGate } from "redux-persist/integration/react";
import { persistStore } from "redux-persist";

import dayjs from "dayjs";
import isLeapYear from 'dayjs/plugin/isLeapYear';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/ko';

import store from './modules';

dayjs.extend(isLeapYear, relativeTime);
dayjs.locale('ko');

const root = ReactDOM.createRoot(document.getElementById('root'));
const persist = persistStore(store);

root.render(
    <>
        <Provider store={store}>
            <PersistGate persistor={persist}>
                <CookiesProvider>
                    <App />
                </CookiesProvider>
            </PersistGate>
        </Provider>
    </>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();

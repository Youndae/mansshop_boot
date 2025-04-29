import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { Provider } from 'react-redux';
import { PersistGate } from "redux-persist/integration/react";
import { CookiesProvider } from "react-cookie";
import { store, persistor } from './app/store';

import dayjs from "dayjs";
import isLeapYear from 'dayjs/plugin/isLeapYear';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/ko';

dayjs.extend(isLeapYear, relativeTime);
dayjs.locale('ko');

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
    <>
        <Provider store={store}>
            <PersistGate persistor={persistor}>
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

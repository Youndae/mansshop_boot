import React from 'react';
import {
    BrowserRouter,
    Routes,
    Route
} from "react-router-dom";

import TestComponent from "./component/TestComponent";
import Login from './component/page/member/Login';
import Join from './component/page/member/Join';
import MyPage from "./component/page/member/MyPage";



function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route index element={<TestComponent />}/>
            <Route path='login' element={<Login />} />
            <Route path='join' element={<Join />} />
            <Route path='member/mypage' element={<MyPage />} />
        </Routes>
      </BrowserRouter>
  )
}
export default App;

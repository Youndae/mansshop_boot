import React from 'react';
import {
    BrowserRouter,
    Routes,
    Route
} from "react-router-dom";

import Navbar from "./component/ui/Navbar";
import Main from "./component/page/main/Main";

import TestComponent from "./component/TestComponent";
import Login from './component/page/member/Login';
import Join from './component/page/member/Join';
import MyPage from "./component/page/member/MyPage";



function App() {
  return (
      <BrowserRouter>
          <div className="container">
              <Navbar />
            <Routes>
              <Route index element={<Main />}/>
                <Route path='login' element={<Login />} />
                <Route path='join' element={<Join />} />
                <Route path='member/mypage' element={<MyPage />} />
            </Routes>
          </div>
      </BrowserRouter>
  )
}
export default App;

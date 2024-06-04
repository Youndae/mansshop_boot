import React from 'react';
import {
    BrowserRouter,
    Routes,
    Route
} from "react-router-dom";

import Navbar from "./component/ui/Navbar";
import Best from "./component/page/main/Best";
import New from './component/page/main/New';
import MainClassification from "./component/page/main/MainClassification";
import SearchProduct from "./component/page/main/SearchProduct";

import Login from './component/page/member/Login';
import Join from './component/page/member/Join';
import MyPage from "./component/page/member/MyPage";
import Oauth from "./component/page/member/Oauth";
import ProductDetail from "./component/page/product/ProductDetail";
import Cart from "./component/page/cart/Cart";

import AdminProduct from "./component/page/admin/AdminProduct";
import AdminMember from "./component/page/admin/AdminMember";



function App() {
  return (
      <BrowserRouter>
          <div className="container">
              <Navbar />
            <Routes>
              <Route index element={<Best />}/>
                <Route path='new' element={<New />} />
                <Route path="search" element={<SearchProduct />} />
                <Route path="search?keyword=:keyword" element={<SearchProduct />} />
                <Route path="search?keyword=:keyword&page=:page" element={<SearchProduct />} />
                <Route path='category/:classification' element={<MainClassification />} />
                <Route path='login' element={<Login />} />
                <Route path='join' element={<Join />} />
                <Route path="oAuth" element={<Oauth />} />
                <Route path='product/:productId' element={<ProductDetail />} />
                <Route path='cart' element={<Cart />} />
                <Route path='member/mypage' element={<MyPage />} />

                <Route path='admin/product' element={<AdminProduct />} />
                <Route path='admin/member' element={<AdminMember />} />
            </Routes>
          </div>
      </BrowserRouter>
  )
}
export default App;

import React from 'react';
import {
    BrowserRouter,
    Routes,
    Route
} from "react-router-dom";

import Navbar from "./component/ui/nav/Navbar";
import Best from "./component/page/main/Best";
import New from './component/page/main/New';
import MainClassification from "./component/page/main/MainClassification";
import SearchProduct from "./component/page/main/SearchProduct";


import Login from './component/page/member/Login';
import Join from './component/page/member/Join';
import Oauth from "./component/page/member/Oauth";
import ProductDetail from "./component/page/product/ProductDetail";
import Cart from "./component/page/cart/Cart";
import Order from "./component/page/order/Order";
import NonMemberOrderInfo from "./component/page/main/NonMemberOrderInfo";
import NonMemberOrderList from "./component/page/main/NonMemberOrderList";

import MyPageOrder from "./component/page/mypage/MyPageOrder";
import LikeProduct from "./component/page/mypage/LikeProduct";
import MyPageProductQnA from "./component/page/mypage/MyPageProductQnA";
import MyPageProductQnADetail from "./component/page/mypage/MyPageProductQnADetail";
import MemberQnA from "./component/page/mypage/MemberQnA";
import MemberQnADetail from "./component/page/mypage/MemberQnADetail";
import MyPageReview from "./component/page/mypage/MyPageReview";
import MyPageUpdateInfo from "./component/page/mypage/MyPageUpdateInfo";

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
                <Route path='productOrder' element={<Order />}/>
                <Route path='order/info' element={<NonMemberOrderInfo />}/>
                <Route path='order/detail' element={<NonMemberOrderList />}/>
                <Route path='order/detail?term=:term' element={<NonMemberOrderList />}/>
                <Route path='order/detail?term=:term&page=:page' element={<NonMemberOrderList />}/>

                <Route path='my-page/order' element={<MyPageOrder />} />
                <Route path='my-page/order?term=:term' element={<MyPageOrder />} />
                <Route path='my-page/order?term=:term&page=:page' element={<MyPageOrder />} />
                <Route path='my-page/like' element={<LikeProduct />}/>
                <Route path='my-page/like?page=:page' element={<LikeProduct />} />
                <Route path='my-page/productQnA' element={<MyPageProductQnA />} />
                <Route path='my-page/productQnA?page=:page' element={<MyPageProductQnA />} />
                <Route path='my-page/productQnA/:qnaId' element={<MyPageProductQnADetail />}/>
                <Route path='my-page/memberQnA' element={<MemberQnA />} />
                <Route path='my-page/memberQnA?page=:page' element={<MemberQnA />} />
                <Route path='my-page/memberQnA/:qnaId' element={<MemberQnADetail />}/>
                <Route path='my-page/review' element={<MyPageReview />} />
                <Route path='my-page/review?page=:page' element={<MyPageReview />} />
                <Route path='my-page/info' element={<MyPageUpdateInfo />} />


                <Route path='admin/product' element={<AdminProduct />} />
                <Route path='admin/member' element={<AdminMember />} />
            </Routes>
          </div>
      </BrowserRouter>
  )
}
export default App;
